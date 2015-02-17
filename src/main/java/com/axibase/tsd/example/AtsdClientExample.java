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
package com.axibase.tsd.example;

import com.axibase.tsd.client.ClientConfigurationFactory;
import com.axibase.tsd.client.DataService;
import com.axibase.tsd.client.HttpClientManager;
import com.axibase.tsd.client.MetaDataService;
import com.axibase.tsd.model.data.GetSeriesResult;
import com.axibase.tsd.model.data.Interval;
import com.axibase.tsd.model.data.IntervalUnit;
import com.axibase.tsd.model.data.Series;
import com.axibase.tsd.model.data.command.GetSeriesCommand;
import com.axibase.tsd.model.meta.EntityAndTags;
import com.axibase.tsd.model.meta.Metric;
import com.axibase.tsd.model.system.ClientConfiguration;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
public class AtsdClientExample {

    private DataService dataService;
    private MetaDataService metaDataService;
    public static final ISO8601DateFormat ISO_DATE_FORMAT = new ISO8601DateFormat();

    public static void main(String[] args) {
        AtsdClientExample atsdClientExample = new AtsdClientExample();
        atsdClientExample.configure();
//        atsdClientExample.printMetaDataAndData("jvm_memory_used_percent");
        atsdClientExample.printMetaDataAndData("disk_used_percent");
    }

    private void configure() {
        System.out.println("Getting Started with Axibase TSD");
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.getInstance().createClientConfiguration();
        HttpClientManager httpClientManager = new HttpClientManager(clientConfiguration);
        dataService = new DataService(httpClientManager);
        metaDataService = new MetaDataService(httpClientManager);
    }

    private void printMetaDataAndData(String metricExample) {
        Metric metric = metaDataService.retrieveMetric(metricExample);
        if (metric == null) {
            System.out.println("Unknown metric: " + metricExample);
            return;
        }
        List<EntityAndTags> entityAndTagsList = metaDataService.retrieveEntityAndTags(metric.getName(), null);
        System.out.println("===Metric MetaData===");
        System.out.println("Metric: " + metric.getName());
        for (EntityAndTags entityAndTags : entityAndTagsList) {
            String entityName = entityAndTags.getEntityName();
            System.out.println("\n===Entity MetaData===");
            System.out.println("Entity: " + entityName);
            Map<String, String> tags = entityAndTags.getTags();
            System.out.println("===Tags===");
            for (Map.Entry<String, String> tagAndValue : tags.entrySet()) {
                System.out.println("\t" + tagAndValue.getKey() + " : " + tagAndValue.getValue());
            }

            System.out.println("===Last Series===");
            GetSeriesCommand command = new GetSeriesCommand(entityName, metric.getName(), tags);
            List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(new Interval(1, IntervalUnit.MINUTE), 10, command);
            for (GetSeriesResult getSeriesResult : getSeriesResults) {
                System.out.println("Time Series Key: " + getSeriesResult.getTimeSeriesKey());
                List<Series> data = getSeriesResult.getData();
                for (Series series : data) {
                    long ts = series.getT();
                    System.out.println(toISODate(ts) + "\t" + series.getV());
                }
            }
        }
    }

    private String toISODate(long time) {
        return ISO_DATE_FORMAT.format(new Date(time));
    }


}
