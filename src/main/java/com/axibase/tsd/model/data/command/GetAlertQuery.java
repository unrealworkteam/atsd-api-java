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

import com.axibase.tsd.model.data.Severity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAlertQuery {
    @JsonProperty(value="metrics")
    private List<String> metricNames;
    @JsonProperty(value="entities")
    private List<String> entityNames;
    @JsonProperty(value="rules")
    private List<String> ruleNames;
    private List<Severity> severities;
    private Severity minSeverity;

    public GetAlertQuery() {
    }

    public GetAlertQuery(List<String> metricNames,
                         List<String> entityNames,
                         List<String> ruleNames,
                         List<Severity> severities,
                         Severity minSeverity) {
        this.metricNames = metricNames;
        this.entityNames = entityNames;
        this.ruleNames = ruleNames;
        this.severities = severities;
        this.minSeverity = minSeverity;
    }

    public List<String> getMetricNames() {
        return metricNames;
    }

    public List<String> getEntityNames() {
        return entityNames;
    }

    public List<String> getRuleNames() {
        return ruleNames;
    }

    public List<Severity> getSeverities() {
        return severities;
    }

    public Severity getMinSeverity() {
        return minSeverity;
    }

    public void setMetricNames(List<String> metricNames) {
        this.metricNames = metricNames;
    }

    public void setEntityNames(List<String> entityNames) {
        this.entityNames = entityNames;
    }

    public void setRuleNames(List<String> ruleNames) {
        this.ruleNames = ruleNames;
    }

    public void setSeverities(List<Severity> severities) {
        this.severities = severities;
    }

    public void setMinSeverity(Severity minSeverity) {
        this.minSeverity = minSeverity;
    }

    @Override
    public String toString() {
        return "GetAlertQuery{" +
                "metricNames=" + metricNames +
                ", entityNames=" + entityNames +
                ", ruleNames=" + ruleNames +
                ", severities=" + severities +
                ", minSeverity=" + minSeverity +
                '}';
    }
}
