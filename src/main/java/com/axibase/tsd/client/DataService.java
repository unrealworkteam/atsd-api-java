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

import com.axibase.tsd.model.data.*;
import com.axibase.tsd.model.data.command.*;
import com.axibase.tsd.query.Query;
import com.axibase.tsd.query.QueryPart;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.axibase.tsd.client.RequestProcessor.patch;
import static com.axibase.tsd.client.RequestProcessor.post;
import static com.axibase.tsd.util.AtsdUtil.check;

/**
 * Provides high-level API to retrieve and update ATSD Data Objects (time-series, alerts, properties).
 *
 * @author Nikolay Malevanny.
 */
public class DataService {
    private HttpClientManager httpClientManager;

    public DataService() {
    }

    public DataService(HttpClientManager httpClientManager) {
        this.httpClientManager = httpClientManager;
    }

    public void setHttpClientManager(HttpClientManager httpClientManager) {
        this.httpClientManager = httpClientManager;
    }

    /**
     * @param startTime     start of the selection interval, specified in Unix (epoch) milliseconds. If startTime is not specified, its set to current server time minus 1 hour.
     * @param endTime       end of the selection interval, specified in Unix (epoch) milliseconds. If endTime is not specified, its set to current server time.
     * @param limit         maximum number of data samples returned. The limit is applied to each series in the response separately.
     * @param seriesQueries queries with details, each query property overrides common one in the request parameters
     * @return list of {@code GetSeriesResult}
     */
    public List<GetSeriesResult> retrieveSeries(Long startTime,
                                                Long endTime,
                                                Integer limit,
                                                GetSeriesCommand... seriesQueries) {
        QueryPart<GetSeriesResult> query = new Query<GetSeriesResult>("series")
                .param("json", "true")
                .param("startTime", startTime)
                .param("endTime", endTime)
                .param("limit", limit);
        return httpClientManager.requestDataList(GetSeriesResult.class, query,
                post(seriesQueries));
    }

    /**
     * @param interval      replaces startTime - endTime values with endTime = now, startTime = now - interval. Format: interval=intervalCount-intervalUnit. Example: Interval=1-hour
     * @param limit         maximum number of data samples returned. The limit is applied to each series in the response separately.
     * @param seriesQueries queries with details, each query property overrides common one in the request parameters
     * @return list of {@code GetSeriesResult}
     */
    public List<GetSeriesResult> retrieveSeries(Interval interval,
                                                Integer limit,
                                                GetSeriesCommand... seriesQueries) {
        QueryPart<GetSeriesResult> query = new Query<GetSeriesResult>("series")
                .param("json", "true")
                .param("interval", interval)
                .param("limit", limit);
        return httpClientManager.requestDataList(GetSeriesResult.class, query,
                post(seriesQueries));
    }

    /**
     * @param addSeriesCommands commands that contains time-series which are added
     * @return true if success
     */
    public boolean addSeries(AddSeriesCommand... addSeriesCommands) {
        QueryPart<GetSeriesResult> query = new Query<GetSeriesResult>("series")
                .path("insert");
        return httpClientManager.updateData(query,
                post(Arrays.asList(addSeriesCommands)));
    }

    /**
     * @param entityName entity name
     * @param data CSV as String
     * @param tagNamesAndValues entity tags
     * @return true if success
     */
    public boolean addSeriesCsv(String entityName, String data, String... tagNamesAndValues) {
        check(entityName, "Entity name is empty");
        check(data, "Data is empty");
        QueryPart<GetSeriesResult> query = new Query<GetSeriesResult>("series")
                .path("csv")
                .path(entityName);
        if (tagNamesAndValues != null) {
            if (tagNamesAndValues.length % 2 == 1) {
                throw new IllegalArgumentException("Tag without value");
            }
            for (int i = 0; i < tagNamesAndValues.length; i++) {
                query = query.param(tagNamesAndValues[i], tagNamesAndValues[++i]);
            }
        }
        return httpClientManager.updateData(query, data);
    }

    /**
     * @param seriesQueries queries with details
     * @return list of {@code GetSeriesResult}
     */
    public List<GetSeriesResult> retrieveLastSeries(GetSeriesCommand... seriesQueries) {
        QueryPart<GetSeriesResult> query = new Query<GetSeriesResult>("series")
                .path("last");
        return httpClientManager.requestDataList(GetSeriesResult.class, query,
                post(seriesQueries));
    }

    /**
     * @param getPropertiesCommand command with property filter parameters
     * @return list of {@code Property}
     */
    public List<Property> retrieveProperties(GetPropertiesCommand getPropertiesCommand) {
        QueryPart<Property> query = new Query<Property>("properties");
        return httpClientManager.requestDataList(Property.class, query,
                post(getPropertiesCommand));
    }

    /**
     * @param properties list of {@code Property} to add.
     * @return true if success
     */
    public boolean insertProperties(Property... properties) {
        QueryPart<Property> query = new Query<Property>("properties")
                .path("insert");
        return httpClientManager.updateData(query, post(Arrays.asList(properties)));
    }

    /**
     * @param batchPropertyCommands list of batch commands to mass update properties
     * @return true if success
     */
    public boolean batchUpdateProperties(BatchPropertyCommand... batchPropertyCommands) {
        QueryPart<Property> query = new Query<Property>("properties");
        return httpClientManager.updateData(query, patch(batchPropertyCommands));
    }

    /**
     * @param metricNames metric filter, multiple values allowed
     * @param entityNames entity filter, multiple values allowed
     * @param ruleNames rule filter, multiple values allowed
     * @param severities severity filter, multiple values allowed
     * @param minSeverity minimal severity filter
     * @return list of {@code Alert}
     */
    public List<Alert> retrieveAlerts(
            List<String> metricNames,
            List<String> entityNames,
            List<String> ruleNames,
            List<Severity> severities,
            Severity minSeverity) {
        QueryPart<Alert> query = new Query<Alert>("/alerts");
        if (minSeverity != null) {
            query.param("min-severity", minSeverity.getCode());
        }
        query = fillParams(query, "metric", metricNames);
        query = fillParams(query, "entity", entityNames);
        query = fillParams(query, "rule", ruleNames);
        if (severities != null) {
            for (Severity severity : severities) {
                query = query.param("severity", severity.getCode());
            }
        }
        return httpClientManager.requestDataList(Alert.class, query, null);
    }

    /**
     * @param getAlertHistoryCommand command with alert history selection details
     * @return list of  {@code AlertHistory}
     */
    public List<AlertHistory> retrieveAlertHistory(GetAlertHistoryCommand getAlertHistoryCommand) {
        QueryPart<AlertHistory> query = new Query<AlertHistory>("alerts")
                .path("history");
        return httpClientManager.requestDataList(AlertHistory.class, query,
                post(getAlertHistoryCommand));
    }

    private static  QueryPart<Alert> fillParams(QueryPart<Alert> query, String paramName, List<String> paramValueList) {
        if (paramValueList != null) {
            for (String paramValue : paramValueList) {
                query = query.param(paramName, paramValue);
            }
        }
        return query;
    }
}
