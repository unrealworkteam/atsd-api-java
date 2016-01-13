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
package com.axibase.tsd.model.data.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Series {
    @JsonProperty("t")
    private Long timeMillis;
    @JsonProperty("d")
    private String date;
    @JsonProperty("v")
    private double value;

    public Series() {
    }

    public Series(long timeMillis, double value) {
        this.timeMillis = timeMillis;
        this.value = value;
    }

    public Long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(Long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Series{" +
                "timeMillis=" + timeMillis +
                ", date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
