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

import com.axibase.tsd.model.data.PropertyParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPropertiesQuery {
    @JsonProperty(value = "entity")
    private final String entityName;
    private final String type;
    private long startTime;
    private long endTime;
    private String limit;
    private boolean last;
    @JsonProperty(value = "keys")
    private Map<String, String> keys;
    @JsonProperty
    private List<String> values;

    public GetPropertiesQuery(String entityName, String type) {
        this.entityName = entityName;
        this.type = type;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getType() {
        return type;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getLimit() {
        return limit;
    }

    public boolean isLast() {
        return last;
    }

    public Map<String, String> getKeys() {
        return keys;
    }

    public List<String> getValues() {
        return values;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "GetPropertiesQuery{" +
                "entityName='" + entityName + '\'' +
                ", type='" + type + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", limit='" + limit + '\'' +
                ", last=" + last +
                ", keys=" + keys +
                ", values=" + values +
                '}';
    }
}
