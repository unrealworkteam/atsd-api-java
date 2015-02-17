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

import com.axibase.tsd.model.data.PropertyKey;
import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutPropertyCommand {
    private PropertyKey key;
    @JsonProperty("values")
    private Map<String,String> values;
    private long timestamp;

    public PutPropertyCommand() {
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

    @JsonIgnore
    public void setValues(String... namesAndValues) {
        this.values = AtsdUtil.toMap(namesAndValues);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PropertyPutCommand{" +
                "key=" + key +
                ", values=" + values +
                ", timestamp=" + timestamp +
                '}';
    }
}
