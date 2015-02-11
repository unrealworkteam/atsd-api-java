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
package com.axibase.tsd.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GetSeriesResult {
    @JsonProperty
    private String id;
    @JsonProperty
    private String requestId;
    @JsonProperty("entity")
    private String entityName;
    @JsonProperty("metric")
    private String metricName;
    @JsonProperty
    private Map<String, String> tags;
    @JsonProperty
    private SeriesType type;
    @JsonProperty
    private Integer intervalCount;
    @JsonProperty
    private IntervalUnit intervalUnit;
    @JsonProperty
    private List<Series> data;

    public GetSeriesResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public SeriesType getType() {
        return type;
    }

    public void setType(SeriesType type) {
        this.type = type;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public List<Series> getData() {
        return data;
    }

    public void setData(List<Series> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GetSeriesResult{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", entityName='" + entityName + '\'' +
                ", metricName='" + metricName + '\'' +
                ", tags=" + tags +
                ", type=" + type +
                ", intervalCount=" + intervalCount +
                ", intervalUnit=" + intervalUnit +
                ", data=" + data +
                '}';
    }
}
