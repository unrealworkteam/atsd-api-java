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
package com.axibase.tsd.util;

import com.axibase.tsd.model.data.series.Series;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
public class AtsdUtil {
    public static final String JSON = MediaType.APPLICATION_JSON;
    public static final String ADD_COMMAND = "add";
    public static final String DELETE_COMMAND = "delete";

    public static Map<String, String> toMap(String... tagNamesAndValues) {
        if (tagNamesAndValues==null || tagNamesAndValues.length==0) {
            return Collections.emptyMap();
        }

        if (tagNamesAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("Key without value");
        }

        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < tagNamesAndValues.length; i++) {
            result.put(tagNamesAndValues[i],tagNamesAndValues[++i]);
        }
        return result;
    }

    public static void check(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkEntityName(String entityName) {
        check(entityName, "Entity name is empty");
    }

    public static void checkEntityGroupName(String entityGroupName) {
        check(entityGroupName, "Entity group name is empty");
    }

    public static void checkMetricName(String metricName) {
        check(metricName, "Metric name is empty");
    }

    public static void checkSeries(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("Series is null");
        }
    }
}
