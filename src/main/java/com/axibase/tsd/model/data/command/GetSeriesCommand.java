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
package com.axibase.tsd.model.data.command;

import com.axibase.tsd.model.data.IntervalUnit;
import com.axibase.tsd.model.data.RateUnit;
import com.axibase.tsd.model.data.SeriesType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSeriesCommand {
    @JsonProperty("entity")
    private String entityName;
    @JsonProperty("metric")
    private String metricName;
    @JsonProperty
    private MultivaluedMap<String, String> tags = new MultivaluedHashMap<String, String>();
    @JsonProperty
    private SeriesType type;
    @JsonProperty
    private Integer intervalCount;
    @JsonProperty
    private IntervalUnit intervalUnit;
    @JsonProperty
    private Integer rateCount;
    @JsonProperty
    private RateUnit rateUnit;
    @JsonProperty
    private String statistics;
    @JsonProperty
    private boolean multipleSeries;

    public GetSeriesCommand() {
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

    public MultivaluedMap<String, String> getTags() {
        return tags;
    }

    public void setTags(MultivaluedMap<String, String> tags) {
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

    public Integer getRateCount() {
        return rateCount;
    }

    public void setRateCount(Integer rateCount) {
        this.rateCount = rateCount;
    }

    public RateUnit getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(RateUnit rateUnit) {
        this.rateUnit = rateUnit;
    }

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public boolean isMultipleSeries() {
        return multipleSeries;
    }

    public void setMultipleSeries(boolean multipleSeries) {
        this.multipleSeries = multipleSeries;
    }

    @Override
    public String toString() {
        return "Series{" +
                "entityName='" + entityName + '\'' +
                ", metricName='" + metricName + '\'' +
                ", tags=" + tags +
                ", type=" + type +
                ", intervalCount=" + intervalCount +
                ", intervalUnit=" + intervalUnit +
                ", rateCount=" + rateCount +
                ", rateUnit=" + rateUnit +
                ", statistics='" + statistics + '\'' +
                ", multipleSeries=" + multipleSeries +
                '}';
    }
}
