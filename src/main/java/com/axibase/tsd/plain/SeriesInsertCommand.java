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

import com.axibase.tsd.model.data.series.Series;
import com.axibase.tsd.util.AtsdUtil;

import java.util.Collections;
import java.util.Map;

import static com.axibase.tsd.util.AtsdUtil.checkEntityName;
import static com.axibase.tsd.util.AtsdUtil.checkMetricName;
import static com.axibase.tsd.util.AtsdUtil.checkSeries;

/**
 * @author Nikolay Malevanny.
 */
public class SeriesInsertCommand implements PlainCommand {
    private final String entityName;
    private final String metricName;
    private final Series series;
    private final Map<String, String> tags;

    public SeriesInsertCommand(String entityName, String metricName, Series series, Map<String, String> tags) {
        checkEntityName(entityName);
        checkMetricName(metricName);
        checkSeries(series);
        this.entityName = entityName;
        this.metricName = metricName;
        this.series = series;
        this.tags = tags == null ? Collections.<String, String>emptyMap() : tags;
    }

    public SeriesInsertCommand(String entityName, String metricName, Series series) {
        this(entityName, metricName, series, Collections.<String, String>emptyMap());
    }

    public SeriesInsertCommand(String entityName, String metricName, Series series, String... tagNamesAndValues) {
        this(entityName, metricName, series, AtsdUtil.toMap(tagNamesAndValues));
    }

    @Override
    public String compose() {
        // series <entity> <metric> <timestamp> <value> <tags>
        StringBuilder sb = new StringBuilder("series ")
                .append(entityName).append(' ')
                .append(metricName).append(' ')
                .append("m:").append(series.getTimeMillis()).append(' ')
                .append(series.getValue());
        for (Map.Entry<String, String> tagNameAndValue : tags.entrySet()) {
            sb.append(' ').append(tagNameAndValue.getKey()).append('=').append(tagNameAndValue.getValue());
        }
        return sb.append('\n').toString();
    }
}
