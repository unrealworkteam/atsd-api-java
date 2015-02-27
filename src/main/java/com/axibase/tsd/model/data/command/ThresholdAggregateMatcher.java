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

import com.axibase.tsd.model.data.series.Interpolate;
import com.axibase.tsd.model.data.series.Interval;
import com.axibase.tsd.model.data.series.aggregate.AggregateType;
import com.axibase.tsd.model.data.series.aggregate.Threshold;
import com.axibase.tsd.model.data.series.aggregate.WorkingMinutes;

/**
 * @author Nikolay Malevanny.
 */
public class ThresholdAggregateMatcher extends SimpleAggregateMatcher {
    protected ThresholdAggregateMatcher(Interval interval, Interpolate interpolate,
                                        AggregateType type, AggregateType... types) {
        super();
        init(interval, interpolate, type, types);
    }

    private WorkingMinutes workingMinutes;
    private Threshold threshold;

    public WorkingMinutes getWorkingMinutes() {
        return workingMinutes;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setWorkingMinutes(WorkingMinutes workingMinutes) {
        this.workingMinutes = workingMinutes;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "ThresholdAggregateMatcher{" +
                super.toString() +
                ", workingMinutes=" + workingMinutes +
                ", threshold=" + threshold +
                '}';
    }
}
