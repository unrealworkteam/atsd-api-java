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
import com.axibase.tsd.model.data.Alert;
import com.axibase.tsd.model.data.AlertHistory;
import com.axibase.tsd.model.data.Property;
import com.axibase.tsd.model.data.PropertyKey;
import com.axibase.tsd.model.data.command.*;
import com.axibase.tsd.model.data.series.*;
import com.axibase.tsd.model.data.series.aggregate.AggregateType;
import com.axibase.tsd.model.meta.Metric;
import com.axibase.tsd.plain.SeriesInsertCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        GetSeriesQuery c1 = createTestGetTestCommand();
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

        Thread.sleep(3000);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(
                new SeriesCommandPreparer() {
                    @Override
                    public void prepare(GetSeriesQuery command) {
//                        command.setAggregateMatcher(new AggregateMatcher(new Interval(20, IntervalUnit.SECOND), Interpolate.NONE, AggregateType.DETAIL));
                        command.setLimit(10);
                    }
                },
                new GetSeriesQuery(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1")),
                new GetSeriesQuery(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM(
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

        Thread.sleep(3000);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(
                new SeriesCommandPreparer() {
                    @Override
                    public void prepare(GetSeriesQuery command) {
                        command.setLimit(10);
                    }
                },
        new GetSeriesQuery(TTT_ENTITY, TTT_METRIC, TestUtil.toMVM("ttt-tag-1", "ttt-tag-value-1"))
        );
        assertEquals(1, getSeriesResults.size());
        assertEquals(10, getSeriesResults.get(0).getData().size());
    }

    @Test
    public void testRetrieveLastSeries() throws Exception {
        GetSeriesQuery c1 = createTestGetTestCommand();
        c1.setAggregateMatcher(null);
        List<GetSeriesResult> seriesList = dataService.retrieveLastSeries(c1);

        assertTrue(seriesList.get(0) instanceof GetSeriesResult);
        assertEquals(1, seriesList.size());
    }

    @Test
    public void testRetrieveProperties() throws Exception {
        GetPropertiesQuery getPropertiesQuery = new GetPropertiesQuery(TTT_ENTITY, TTT_TYPE);
        getPropertiesQuery.setStartTime(0);
        getPropertiesQuery.setEndTime(Long.MAX_VALUE);
        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("key1", "ttt-key-1");
        getPropertiesQuery.setKeys(keys);
        List<Property> properties = dataService.retrieveProperties(getPropertiesQuery);
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

    public GetPropertiesQuery createGetNewPropCommand() {
        GetPropertiesQuery getPropertiesQuery = new GetPropertiesQuery(NNN_ENTITY, NNN_TYPE);
        getPropertiesQuery.setStartTime(0);
        getPropertiesQuery.setEndTime(Long.MAX_VALUE);
        return getPropertiesQuery;
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

    @Ignore
    @Test
    public void testRetrieveAlertHistory() throws Exception {
        GetAlertHistoryQuery getAlertHistoryQuery = new GetAlertHistoryQuery();
        getAlertHistoryQuery.setStartTime(0L);
        getAlertHistoryQuery.setEndTime(Long.MAX_VALUE);
        getAlertHistoryQuery.setEntityName(TTT_ENTITY);
        getAlertHistoryQuery.setMetricName(TTT_METRIC);

        List<AlertHistory> alertHistoryList = dataService.retrieveAlertHistory(getAlertHistoryQuery);
        assertTrue(alertHistoryList.get(0) instanceof AlertHistory);
        assertTrue(alertHistoryList.size() > 0);
    }

    public GetSeriesQuery createTestGetTestCommand() {
        MultivaluedHashMap<String, String> tags = new MultivaluedHashMap<String, String>();
        tags.add("ttt-tag-1", "ttt-tag-value-1");
        tags.add("ttt-tag-2", "ttt-tag-value-2");
        GetSeriesQuery command = new GetSeriesQuery(TTT_ENTITY, TTT_METRIC);
        command.setTags(tags);
        command.setAggregateMatcher(new SimpleAggregateMatcher(new Interval(20, IntervalUnit.SECOND),
                Interpolate.LINEAR,
                AggregateType.DETAIL));
        return command;
    }

    @Test
    public void testMultiThreadStreamingCommands() throws Exception {
        final int size = 5;
        final int cnt = 300;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(size, size, 0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(cnt));
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(cnt);
        for (int i = 0; i < cnt; i++) {
            threadPoolExecutor.execute(new SimpleSeriesSender(start, dataService, i, latch));
        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        Thread.sleep(3000);

        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(
                new SeriesCommandPreparer() {
                    @Override
                    public void prepare(GetSeriesQuery command) {
                        command.setStartTime(System.currentTimeMillis() - 10000);
                        command.setEndTime(System.currentTimeMillis() + 1000);
                        MultivaluedHashMap<String, String> tags = new MultivaluedHashMap<String, String>();
                        for (int i = 0; i < size; i++) {
                            tags.add("thread","pool-1-thread-" + (1+i));
//                            tags.add("thread","pool-1-thread-" + (i));
                        }
                        command.setTags(tags);
                    }
                }, new GetSeriesQuery(SSS_ENTITY, SSS_METRIC));
        List<GetSeriesResult> results = getSeriesResults;
        int resCnt = 0;
        for (GetSeriesResult result : results) {
            resCnt+= result.getData().size();
        }
        assertEquals(cnt, resCnt);
    }

    @After
    public void tearDown() throws Exception {
        httpClientManager.close();
    }

    private static class SimpleSeriesSender implements Runnable {
        private static AtomicInteger counter = new AtomicInteger(0);
        private long startMs;
        private final DataService dataService;
        private final int num;
        private CountDownLatch latch;

        public SimpleSeriesSender(long startMs, DataService dataService, int num, CountDownLatch latch) {
            this.startMs = startMs;
            this.dataService = dataService;
            this.num = num;
            this.latch = latch;
        }

        @Override
        public void run() {
            Series series = new Series(startMs + counter.incrementAndGet(), Math.random());
            SeriesInsertCommand seriesInsertCommand = new SeriesInsertCommand(SSS_ENTITY, SSS_METRIC, series,
                    "thread", Thread.currentThread().getName());
            dataService.sendPlainCommand(seriesInsertCommand);
            latch.countDown();
        }
    }
}