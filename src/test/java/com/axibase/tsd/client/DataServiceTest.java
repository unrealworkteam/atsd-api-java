/*
* Copyright 2015 Axibase Corporation or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License").
* You may not use this file except in compliance with the License.
* A copy of the License is located at
*
* https://www.axibase.com/atsd/axibase-apache-2.0.pdf
*
* or in the "license" file accompanying this file. This file is distributed
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
package com.axibase.tsd.client;

import com.axibase.tsd.TestUtil;
import com.axibase.tsd.model.data.*;
import com.axibase.tsd.model.data.command.*;
import com.axibase.tsd.model.data.PropertyParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.axibase.tsd.TestUtil.*;
import static junit.framework.Assert.*;

public class DataServiceTest {

    private DataService dataService;
    private HttpClientManager httpClientManager;

    @Before
    public void setUp() throws Exception {
        dataService = new DataService();
        httpClientManager = TestUtil.buildHttpClientManager();
        dataService.setHttpClientManager(httpClientManager);
    }

    @Test
    public void testRetrieveSeries() throws Exception {
        GetSeriesCommand c1 = createTestGetTestCommand();
        List<GetSeriesResult> seriesList = dataService.retrieveSeries(0L,
                System.currentTimeMillis(),
                null,
                100,
                c1);

        assertTrue(seriesList.get(0) instanceof GetSeriesResult);
        assertTrue(seriesList.size() > 0);
    }

    @Test
    public void testInsertSeries() throws Exception {
        long ct = System.currentTimeMillis();
        AddSeriesCommand c1 = new AddSeriesCommand(TTT_ENTITY, TTT_METRIC, "ttt-tag-1", "ttt-tag-value-1");
        int testCnt = 10;
        for (int i = 0; i < testCnt; i++) {
            c1.addSeries(
                    new Series(ct + i, i)
            );
        }
        AddSeriesCommand c2 = new AddSeriesCommand(TTT_ENTITY, TTT_METRIC
                , "ttt-tag-1", "ttt-tag-value-1"
                , "ttt-tag-2", "ttt-tag-value-2"
        );
        for (int i = 0; i < testCnt; i++) {
            c2.addSeries(
                    new Series(ct + i, i * i)
            );
        }
        dataService.addSeries(c1, c2);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(ct, ct + 9, null, 20,
                new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1")),
                new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM(
                         "ttt-tag-1", "ttt-tag-value-1"
                        , "ttt-tag-2", "ttt-tag-value-2"))
                );
        assertEquals(2, getSeriesResults.size());
        assertEquals(10, getSeriesResults.get(0).getData().size());
        assertEquals(10, getSeriesResults.get(1).getData().size());
    }

    //@Test // under construction
    public void testInsertSeriesCsv() throws Exception {
        long ct = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("time, ").append(TTT_METRIC).append('\n');
        int testCnt = 10;
        for (int i = 0; i < testCnt; i++) {
            sb.append(ct + i).append(",").append(i * i * i).append('\n');
        }

        dataService.addSeriesCsv(TTT_ENTITY, sb.toString(), "ttt-tag-1", "ttt-tag-value-1");

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(ct, ct + 9, null, 10,
                new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1"))
        );
        assertEquals(1, getSeriesResults.size());
        assertEquals(10, getSeriesResults.get(0).getData().size());
    }

    @Test
    public void testRetrieveLastSeries() throws Exception {
        GetSeriesCommand c1 = createTestGetTestCommand();
        List<GetSeriesResult> seriesList = dataService.retrieveLastSeries(c1);

        assertTrue(seriesList.get(0) instanceof GetSeriesResult);
        assertEquals(1, seriesList.size());
    }

    @Test
    public void testRetrieveProperties() throws Exception {
        GetPropertiesCommand getPropertiesCommand = new GetPropertiesCommand();
        getPropertiesCommand.setStartTime(0);
        getPropertiesCommand.setEndTime(Long.MAX_VALUE);
//        getPropertiesCommand.setLast(true);
        ArrayList<PropertyParameter> params = new ArrayList<PropertyParameter>();
        PropertyParameter p1 = new PropertyParameter();
        p1.setType(TTT_TYPE);
        p1.setEntityName(TTT_ENTITY);
//        p1.setLimit("1");
        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("key1", "ttt-key-1");
//      //  p1.setKeys(keys);
        params.add(p1);
        getPropertiesCommand.setParams(params);
        List<Property> properties = dataService.retrieveProperties(getPropertiesCommand);
        for (Property property : properties) {
            System.out.println("property = " + property);
        }
        assertTrue(properties.get(0) instanceof Property);
        assertEquals(1, properties.size());
    }

    // @Test //  under construction
    public void testInsertProperties() throws Exception {
        long lastObjectTimestamp = 0;
        {
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(0, properties.size());
        }
        {
            PatchPropertiesCommand patchPropertiesCommand;
            patchPropertiesCommand = new PatchPropertiesCommand();
            patchPropertiesCommand.setPuts(createPuts());
            boolean result = dataService.insertProperties(patchPropertiesCommand);
            assertTrue(result);
        }
        {
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            lastObjectTimestamp = properties.get(0).getTimestamp();
            assertEquals(1, properties.size());
        }
        {
            PatchPropertiesCommand patchPropertiesCommand = new PatchPropertiesCommand();
            DeletePropertyCommand deletePropertyCommand = new DeletePropertyCommand();
            deletePropertyCommand.setKeys(Arrays.asList("nnn-test-key-1"));
            deletePropertyCommand.setTimestamp(lastObjectTimestamp + 100000L);
            patchPropertiesCommand.setPuts(createPuts());
            patchPropertiesCommand.setDelete(deletePropertyCommand);
            boolean result = dataService.insertProperties(patchPropertiesCommand);
            assertTrue(result);
        }
        {
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(0, properties.size());
        }
    }

    public List<PutPropertyCommand> createPuts() {
        PutPropertyCommand putPropertyCommand = new PutPropertyCommand();
        putPropertyCommand.setKey(new PropertyKey(NNN_TYPE, TTT_ENTITY, "nnn-test-key-1", "nnn-test-key-value-1"));
        putPropertyCommand.setValues("nnn-name", "nnn-value");
        return Arrays.asList(putPropertyCommand);
    }

    public GetPropertiesCommand createGetNewPropCommand() {
        GetPropertiesCommand getPropertiesCommand = new GetPropertiesCommand();
        getPropertiesCommand.setStartTime(0);
        getPropertiesCommand.setEndTime(Long.MAX_VALUE);
        PropertyParameter propertyParameter = new PropertyParameter();
        propertyParameter.setEntityName(TTT_ENTITY);
        propertyParameter.setType(NNN_TYPE);
        getPropertiesCommand.setParams(propertyParameter);
        return getPropertiesCommand;
    }


    @Test
    public void testRetrieveAlerts() throws Exception {
        {
            List<String> metrics = Arrays.asList(TTT_METRIC);
            List<String> entities = Arrays.asList(TTT_ENTITY);
            List<Alert> alerts = dataService.retrieveAlerts(metrics, entities, null, null, null);
            assertNotNull(alerts);
        }
        {
            List<Alert> alerts = dataService.retrieveAlerts(null, null, null, null, null);
            assertNotNull(alerts);
        }
    }

    @Test
    public void testRetrieveAlertHistory() throws Exception {
        GetAlertHistoryCommand getAlertHistoryCommand = new GetAlertHistoryCommand();
        getAlertHistoryCommand.setStartTime(0L);
        getAlertHistoryCommand.setEndTime(Long.MAX_VALUE);
        getAlertHistoryCommand.setEntityName(TTT_ENTITY);
        getAlertHistoryCommand.setMetricName(TTT_METRIC);

        List<AlertHistory> alertHistoryList = dataService.retrieveAlertHistory(getAlertHistoryCommand);
        assertTrue(alertHistoryList.get(0) instanceof AlertHistory);
        assertTrue(alertHistoryList.size() > 0);
    }

    public GetSeriesCommand createTestGetTestCommand() {
        GetSeriesCommand command = new GetSeriesCommand();
        command.setEntityName(TTT_ENTITY);
        command.setMetricName(TTT_METRIC);
        MultivaluedHashMap<String, String> tags = new MultivaluedHashMap<String, String>();
        tags.add("ttt-tag-1", "ttt-tag-value-1");
        tags.add("ttt-tag-2", "ttt-tag-value-2");
        command.setTags(tags);
        command.setMultipleSeries(true);
        command.setIntervalUnit(IntervalUnit.SECOND);
        return command;
    }

    @After
    public void tearDown() throws Exception {
        httpClientManager.close();
    }
}