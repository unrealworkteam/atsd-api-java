/*
 * Copyright 2016 Axibase Corporation or its affiliates. All Rights Reserved.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAlertHistoryQuery {
    private Long startTime;
    private Long endTime;
    @JsonProperty("metric")
    private String metricName;
    @JsonProperty("entity")
    private String entityName;
    @JsonProperty("entityGroup")
    private String entityGroupName;
    @JsonProperty("rule")
    private String ruleName;
    private Integer limit;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityGroupName() {
        return entityGroupName;
    }

    public void setEntityGroupName(String entityGroupName) {
        this.entityGroupName = entityGroupName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "GetAlertHistoryQuery{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", metricName='" + metricName + '\'' +
                ", entityName='" + entityName + '\'' +
                ", entityGroupName='" + entityGroupName + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", limit=" + limit +
                '}';
    }
}
