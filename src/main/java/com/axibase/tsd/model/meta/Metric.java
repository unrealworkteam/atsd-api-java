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
package com.axibase.tsd.model.meta;

import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Metric {
    @JsonProperty
    private String name;
    @JsonProperty
    private String label;
    @JsonProperty
    private Boolean enabled;
    @JsonProperty("data_type")
    private DataType dataType;
    @JsonProperty("time_precision")
    private TimePrecision timePrecision;
    @JsonProperty
    private Boolean persistent;
    @JsonProperty
    private String filter;
    @JsonProperty("min_value")
    private Double minValue;
    @JsonProperty("max_value")
    private Double maxValue;
    @JsonProperty("invalid_action")
    private InvalidAction invalidAction;
    @JsonProperty
    private String description;
    @JsonProperty("retention_interval")
    private Integer retentionInterval;
    @JsonProperty("last_insert_time")
    private Long lastInsertTime;
    @JsonProperty

    private Map<String, String> tags;

    public Metric() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public TimePrecision getTimePrecision() {
        return timePrecision;
    }

    public void setTimePrecision(TimePrecision timePrecision) {
        this.timePrecision = timePrecision;
    }

    public Boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(Boolean persistent) {
        this.persistent = persistent;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public InvalidAction getInvalidAction() {
        return invalidAction;
    }

    public void setInvalidAction(InvalidAction invalidAction) {
        this.invalidAction = invalidAction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRetentionInterval() {
        return retentionInterval;
    }

    public void setRetentionInterval(Integer retentionInterval) {
        this.retentionInterval = retentionInterval;
    }

    public Long getLastInsertTime() {
        return lastInsertTime;
    }

    public void setLastInsertTime(Long lastInsertTime) {
        this.lastInsertTime = lastInsertTime;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public void setTags(String... tagNamesAndValues) {
        setTags(AtsdUtil.toMap(tagNamesAndValues));
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", enabled=" + enabled +
                ", dataType=" + dataType +
                ", timePrecision=" + timePrecision +
                ", persistent=" + persistent +
                ", filter='" + filter + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", invalidAction=" + invalidAction +
                ", description='" + description + '\'' +
                ", retentionInterval=" + retentionInterval +
                ", lastInsertTime=" + lastInsertTime +
                ", tags=" + tags +
                '}';
    }
}