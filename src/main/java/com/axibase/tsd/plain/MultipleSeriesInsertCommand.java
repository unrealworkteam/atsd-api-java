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

package com.axibase.tsd.plain;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
public class MultipleSeriesInsertCommand extends AbstractSeriesInsertCommand  {
    private final Map<String, Double> metricsAndValues;

    public MultipleSeriesInsertCommand(String entityName, long time, Map<String, String> tags,
                                       Map<String, Double> metricsAndValues) {
        super(entityName, time, tags);
        this.metricsAndValues = metricsAndValues;
    }

    @Override
    protected void addValues(StringBuilder sb) {
        for (Map.Entry<String, Double> metricNameAndValue : metricsAndValues.entrySet()) {
            sb.append(" m:").append(metricNameAndValue.getKey()).append('=').append(metricNameAndValue.getValue());
        }
    }
}
