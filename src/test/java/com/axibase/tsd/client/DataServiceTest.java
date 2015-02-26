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
import com.axibase.tsd.model.data.series.*;
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
        List<GetSeriesResult> seriesList = dataService.retrieveSeries(c1);

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

        Thread.sleep(1000);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(
                new SeriesCommandPreparer() {
                    @Override
                    public void prepare(GetSeriesCommand command) {
//                        command.setAggregateMatcher(new AggregateMatcher(new Interval(20, IntervalUnit.SECOND), Interpolate.NONE, AggregateType.DETAIL));
                        command.setLimit(10);
                    }
                },
                new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1")),
                new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM(
                        "ttt-tag-1", "ttt-tag-value-1"
                        , "ttt-tag-2", "ttt-tag-value-2"))
        );
        assertEquals(2, getSeriesResults.size());
        assertEquals(10, getSeriesResults.get(0).getData().size());
        assertEquals(10, getSeriesResults.get(1).getData().size());
    }

    @Test
    public void testInsertSeriesCsv() throws Exception {
        long ct = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("time, ").append(TTT_METRIC).append('\n');
        int testCnt = 10;
        for (int i = 0; i < testCnt; i++) {
            sb.append(ct + i).append(",").append(i * i * i).append('\n');
        }

        dataService.addSeriesCsv(TTT_ENTITY, sb.toString(), "ttt-tag-1", "ttt-tag-value-1");

        Thread.sleep(1000);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(
                new SeriesCommandPreparer() {
                    @Override
                    public void prepare(GetSeriesCommand command) {
                        command.setLimit(10);
                    }
                },
        new GetSeriesCommand(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1"))
        );
        assertEquals(1, getSeriesResults.size());
        assertEquals(10, getSeriesResults.get(0).getData().size());
    }

    @Test
    public void testRetrieveLastSeries() throws Exception {
        GetSeriesCommand c1 = createTestGetTestCommand();
        c1.setAggregateMatcher(null);
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

    @Test
    public void testInsertProperties() throws Exception {
        PropertyKey nnnKey = new PropertyKey(NNN_TYPE, NNN_ENTITY, "nnn-test-key-1", "nnn-test-key-value-1");
        { // check that new property does not exist
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(0, properties.size());
        }
        { // create new property
            assertTrue(dataService.insertProperties(new Property(nnnKey, "nnn-name", "nnn-value")));
        }
        { // check that new property exists
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(1, properties.size());
        }
        { // delete property
            BatchPropertyCommand deletePropertyCommand = BatchPropertyCommand.createDeleteCommand(
                    new Property(nnnKey, "nnn-name", "nnn-value")
            );
            assertTrue(dataService.batchUpdateProperties(deletePropertyCommand));
        }
        { // check that new property does not exist
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(0, properties.size());
        }
        { // create new property
            assertTrue(dataService.insertProperties(new Property(nnnKey, "nnn-name", "nnn-value")));
        }
        { // delete property using matcher
            BatchPropertyCommand deletePropertyCommand = BatchPropertyCommand.createDeleteMatchCommand(
                    new PropertyMatcher(nnnKey, Long.MAX_VALUE)
            );
            assertTrue(dataService.batchUpdateProperties(deletePropertyCommand));
        }
        { // check that new property does not exist
            List<Property> properties = dataService.retrieveProperties(createGetNewPropCommand());
            assertEquals(0, properties.size());
        }
    }

    public GetPropertiesCommand createGetNewPropCommand() {
        GetPropertiesCommand getPropertiesCommand = new GetPropertiesCommand();
        getPropertiesCommand.setStartTime(0);
        getPropertiesCommand.setEndTime(Long.MAX_VALUE);
        PropertyParameter propertyParameter = new PropertyParameter();
        propertyParameter.setEntityName(NNN_ENTITY);
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
        MultivaluedHashMap<String, String> tags = new MultivaluedHashMap<String, String>();
        tags.add("ttt-tag-1", "ttt-tag-value-1");
        tags.add("ttt-tag-2", "ttt-tag-value-2");
        GetSeriesCommand command = new GetSeriesCommand(TTT_ENTITY, TTT_METRIC);
        command.setTags(tags);
        command.setAggregateMatcher(new AggregateMatcher(new Interval(20, IntervalUnit.SECOND), Interpolate.LINEAR, AggregateType.DETAIL));
        return command;
    }

    @After
    public void tearDown() throws Exception {
        httpClientManager.close();
    }
}