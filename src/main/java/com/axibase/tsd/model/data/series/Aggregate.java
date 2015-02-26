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
package com.axibase.tsd.model.data.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Contains data to compute statistics for the specified time intervals.
 *
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Aggregate {
    private AggregateType type;
    private Interval interval;
    private Interpolate interpolate;

    public Aggregate() {
    }

    public AggregateType getType() {
        return type;
    }

    /**
     * interval for computing statistics.
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Generates missing aggregation intervals using interpolation if enabled: NONE, LINEAR, STEP
     */
    public Interpolate getInterpolate() {
        return interpolate;
    }

    public void setType(AggregateType type) {
        this.type = type;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public void setInterpolate(Interpolate interpolate) {
        this.interpolate = interpolate;
    }

    @Override
    public String toString() {
        return "Aggregate{" +
                "type=" + type +
                ", interval=" + interval +
                ", interpolate=" + interpolate +
                '}';
    }
}
