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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PropertyKey {
    private String type;
    @JsonProperty("entity")
    private String entityName;
    private Map<String, String> keyValues;

    public PropertyKey() {
    }

    public PropertyKey(String type, String entityName, String... keysAndValues) {
        if (keysAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("Last key value is missing");
        }
        this.type = type;
        this.entityName = entityName;
        this.keyValues = new HashMap<String, String>();
        for (int i = 0; i < keysAndValues.length; i++) {
            keyValues.put(keysAndValues[i++], keysAndValues[i]);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, String> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public String toString() {
        return "PropertyKey{" +
                "type='" + type + '\'' +
                ", entityName='" + entityName + '\'' +
                ", keyValues=" + keyValues +
                '}';
    }
}
