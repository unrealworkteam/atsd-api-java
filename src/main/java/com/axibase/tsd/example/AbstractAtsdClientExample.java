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
import com.axibase.tsd.model.data.Series;
import com.axibase.tsd.model.system.ClientConfiguration;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
public abstract class AbstractAtsdClientExample {
    public static final ISO8601DateFormat ISO_DATE_FORMAT = new ISO8601DateFormat();
    protected DataService dataService;
    protected MetaDataService metaDataService;

    protected void configure() {
        System.out.println("Getting Started with Axibase TSD");
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.getInstance().createClientConfiguration();
        System.out.println("Connecting to ATSD: " + clientConfiguration.getMetaDataUrl());
        HttpClientManager httpClientManager = new HttpClientManager(clientConfiguration);
        dataService = new DataService(httpClientManager);
        metaDataService = new MetaDataService(httpClientManager);
    }

    protected String toISODate(long time) {
        return ISO_DATE_FORMAT.format(new Date(time));
    }

    protected void print(GetSeriesResult getSeriesResult) {
        System.out.println("Time Series Key: " + getSeriesResult.getTimeSeriesKey());
        List<Series> data = getSeriesResult.getData();
        for (Series series : data) {
            long ts = series.getT();
            System.out.println(toISODate(ts) + "\t" + series.getV());
        }
    }
}
