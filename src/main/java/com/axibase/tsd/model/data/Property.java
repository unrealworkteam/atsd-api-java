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

import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {
    private PropertyKey key;
    private Map<String, String> values;
    private long timestamp;

    public Property() {
    }

    public Property(PropertyKey key, String... namesAndValues) {
        this.key = key;
        this.values = AtsdUtil.toMap(namesAndValues);
    }

    public PropertyKey getKey() {
        return key;
    }

    public void setKey(PropertyKey key) {
        this.key = key;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Property{" +
                "key=" + key +
                ", values=" + values +
                ", timestamp=" + timestamp +
                '}';
    }
}
