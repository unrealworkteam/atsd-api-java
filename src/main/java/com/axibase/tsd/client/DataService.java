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

import com.axibase.tsd.model.data.Alert;
import com.axibase.tsd.model.data.AlertHistory;
import com.axibase.tsd.model.data.Property;
import com.axibase.tsd.model.data.Severity;
import com.axibase.tsd.model.data.command.*;
import com.axibase.tsd.model.data.series.GetSeriesBatchResult;
import com.axibase.tsd.model.data.series.GetSeriesResult;
import com.axibase.tsd.query.Query;
import com.axibase.tsd.query.QueryPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.axibase.tsd.client.RequestProcessor.patch;
import static com.axibase.tsd.client.RequestProcessor.post;
import static com.axibase.tsd.util.AtsdUtil.check;

/**
 * Provides high-level API to retrieve and update ATSD Data Objects (time-series, alerts, properties).
 *
 * @author Nikolay Malevanny.
 */
public class DataService {
    public static final SeriesCommandPreparer LAST_PREPARER = new LastPreparer();

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
     * @param seriesQueries queries with details, each query property overrides common one in the request parameters
     * @return list of {@code GetSeriesResult}
     */
    public List<GetSeriesResult> retrieveSeries(GetSeriesQuery... seriesQueries) {
        QueryPart<GetSeriesBatchResult> query = new Query<GetSeriesBatchResult>("series");
        GetSeriesBatchResult seriesBatchResult = httpClientManager.requestData(GetSeriesBatchResult.class, query,
                post(new BatchQuery<GetSeriesQuery>(Arrays.asList(seriesQueries))));
        return seriesBatchResult.getSeriesResults();
    }

    public List<GetSeriesResult> retrieveSeries(SeriesCommandPreparer preparer, GetSeriesQuery... seriesQueries) {
        if (preparer != null) {
            for (GetSeriesQuery seriesQuery : seriesQueries) {
                preparer.prepare(seriesQuery);
            }
        }
        return retrieveSeries(seriesQueries);
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
    public List<GetSeriesResult> retrieveLastSeries(GetSeriesQuery... seriesQueries) {
        return retrieveSeries(LAST_PREPARER, seriesQueries);
    }

    /**
     * @param getPropertiesQuery command with property filter parameters
     * @return list of {@code Property}
     */
    public List<Property> retrieveProperties(GetPropertiesQuery getPropertiesQuery, GetPropertiesQuery... getPropertiesQueries) {
        QueryPart<Property> query = new Query<Property>("properties");
        return httpClientManager.requestDataList(Property.class, query,
                post(new BatchQuery<GetPropertiesQuery>(getPropertiesQuery, getPropertiesQueries)));
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
        GetAlertQuery alertQuery = new GetAlertQuery(metricNames, entityNames, ruleNames, severities, minSeverity);
        return retrieveAlerts(alertQuery);
    }

    public List<Alert> retrieveAlerts(GetAlertQuery alertQuery, GetAlertQuery... alertQueries) {
        QueryPart<Alert> query = new Query<Alert>("/alerts");
        BatchQuery<GetAlertQuery> batchQuery = new BatchQuery<GetAlertQuery>(alertQuery, alertQueries);
        return httpClientManager.requestDataList(Alert.class, query, post(batchQuery));
    }

    /**
     * @param getAlertHistoryQuery command with alert history selection details
     * @return list of  {@code AlertHistory}
     */
    public List<AlertHistory> retrieveAlertHistory(GetAlertHistoryQuery getAlertHistoryQuery,
                                                   GetAlertHistoryQuery... getAlertHistoryQueries) {
        QueryPart<AlertHistory> query = new Query<AlertHistory>("alerts")
                .path("history");
        return httpClientManager.requestDataList(AlertHistory.class, query,
                post(new BatchQuery<GetAlertHistoryQuery>(getAlertHistoryQuery, getAlertHistoryQueries)));
    }

    private static  QueryPart<Alert> fillParams(QueryPart<Alert> query, String paramName, List<String> paramValueList) {
        if (paramValueList != null) {
            for (String paramValue : paramValueList) {
                query = query.param(paramName, paramValue);
            }
        }
        return query;
    }

    private static class LastPreparer implements SeriesCommandPreparer {
        @Override
        public void prepare(GetSeriesQuery command) {
            command.setLast(true);
        }
    }
}
