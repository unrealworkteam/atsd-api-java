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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSeriesCommand {
    @JsonProperty("entity")
    private String entityName;
    @JsonProperty("metric")
    private String metricName;
    private MultivaluedMap<String, String> tags = new MultivaluedHashMap<String, String>();
    private SeriesType type;
    private Integer intervalCount;
    private IntervalUnit intervalUnit;
    private Integer rateCount;
    private RateUnit rateUnit;
    private String statistics;
    private boolean multipleSeries;

    public GetSeriesCommand() {
    }

    /**
     * @param entityName
     * @param metricName
     * @param tags
     */
    public GetSeriesCommand(String entityName, String metricName, MultivaluedMap<String, String> tags) {
        this.entityName = entityName;
        this.metricName = metricName;
        this.tags = tags;
    }

    /**
     * @param entityName
     * @param metricName
     * @param tags
     */
    public GetSeriesCommand(String entityName, String metricName, Map<String, String> tags) {
        this.entityName = entityName;
        this.metricName = metricName;
        this.tags = new MultivaluedHashMap<String, String>(tags);
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

    /**
     * @param type
     */
    public void setType(SeriesType type) {
        this.type = type;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    /**
     * @param intervalCount
     */
    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    /**
     * @param intervalUnit
     */
    public void setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public Integer getRateCount() {
        return rateCount;
    }

    /**
     * @param rateCount
     */
    public void setRateCount(Integer rateCount) {
        this.rateCount = rateCount;
    }

    public RateUnit getRateUnit() {
        return rateUnit;
    }

    /**
     * @param rateUnit
     */
    public void setRateUnit(RateUnit rateUnit) {
        this.rateUnit = rateUnit;
    }

    public String getStatistics() {
        return statistics;
    }

    /**
     * @param statistics
     */
    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public boolean isMultipleSeries() {
        return multipleSeries;
    }

    /**
     * @param multipleSeries
     */
    public void setMultipleSeries(boolean multipleSeries) {
        this.multipleSeries = multipleSeries;
    }

    @JsonIgnore
    public void setInterval(IntervalUnit unit, int count) {
        setIntervalUnit(unit);
        setIntervalCount(count);
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
