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

import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyMatcher {
    private String type;
    @JsonProperty("entity")
    private String entityName;
    private Map<String, String> key;
    private Long createdBeforeTime;

    public PropertyMatcher() {
    }

    public PropertyMatcher(String type, String entityName, Long createdBeforeTime, String... keyNamesAnaValues) {
        this.type = type;
        this.entityName = entityName;
        this.createdBeforeTime = createdBeforeTime;
        this.key = AtsdUtil.toMap(keyNamesAnaValues);
    }

    public String getType() {
        return type;
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, String> getKey() {
        return key;
    }

    public Long getCreatedBeforeTime() {
        return createdBeforeTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setKey(Map<String, String> key) {
        this.key = key;
    }

    public void setCreatedBeforeTime(Long createdBeforeTime) {
        this.createdBeforeTime = createdBeforeTime;
    }

    @Override
    public String toString() {
        return "PropertyMatcher{" +
                "type='" + type + '\'' +
                ", entityName='" + entityName + '\'' +
                ", key=" + key +
                ", createdBeforeTime=" + createdBeforeTime +
                '}';
    }
}
