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
package com.axibase.tsd.util;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.*;

@Slf4j
public class AtsdUtil {
    private static final String CLASSPATH_PREFIX = "classpath:";
    public static final String ADD_COMMAND = "add";
    public static final String DELETE_COMMAND = "delete";
    public static final String MARKER_KEYWORD = "marker ";
    public static final String PING_COMMAND = "ping\n";

    public static Map<String, String> toMap(String... tagNamesAndValues) {
        if (ArrayUtils.isEmpty(tagNamesAndValues)) {
            return Collections.emptyMap();
        }

        if (tagNamesAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("Key without value");
        }

        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < tagNamesAndValues.length; i++) {
            result.put(tagNamesAndValues[i], tagNamesAndValues[++i]);
        }
        return result;
    }

    public static Map<String, Double> toValuesMap(Object... metricNamesAndValues) {
        if (ArrayUtils.isEmpty(metricNamesAndValues)) {
            return Collections.emptyMap();
        }

        if (metricNamesAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("Key without value");
        }

        Map<String, Double> result = new HashMap<String, Double>();
        for (int i = 0; i < metricNamesAndValues.length; i++) {
            result.put((String) metricNamesAndValues[i], (Double) metricNamesAndValues[++i]);
        }
        return result;
    }

    public static void check(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkPropertyTypeIsEmpty(String type) {
        check(type, "Type is empty");
    }

    public static void checkEntityIsEmpty(String entityName) {
        check(entityName, "Entity name is empty");
    }

    public static void checkEntityGroupIsEmpty(String entityGroupName) {
        check(entityGroupName, "Entity group name is empty");
    }

    public static void checkMetricIsEmpty(String metricName) {
        check(metricName, "Metric name is empty");
    }

    public static class DateTime {
        public static final String MIN_QUERIED_DATE_TIME = "1000-01-01T00:00:00.000Z";
        public static final String MAX_QUERIED_DATE_TIME = "9999-12-31T23:59:59.999Z";

        public static Date parseDate(String date) {
            try {
                return ISO8601Utils.parse(date, new ParsePosition(0));
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        public static String isoFormat(Date date) {
            return isoFormat(date, "GMT");
        }

        public static String isoFormat(Date date, String timeZoneName) {
            return isoFormat(date, true, timeZoneName);
        }

        public static String isoFormat(Date date, boolean withMillis, String timeZoneName) {
            return ISO8601Utils.format(date, withMillis, TimeZone.getTimeZone(timeZoneName));
        }
    }

    public static String formatMetricValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "NaN";
        }
        return Double.toString(value);
    }

    public static Properties loadProperties(String clientPropertiesFileName) {
        log.debug("Load client properties from file: {}", clientPropertiesFileName);
        Properties clientProperties = new Properties();
        InputStream stream = null;
        try {
            if (clientPropertiesFileName.startsWith(CLASSPATH_PREFIX)) {
                String resourcePath = clientPropertiesFileName.split(CLASSPATH_PREFIX)[1];
                log.info("Load properties from classpath: {}", resourcePath);
                stream = AtsdUtil.class.getResourceAsStream(resourcePath);
            } else {
                File file = new File(clientPropertiesFileName);
                log.info("Load properties from file: {}", file.getAbsolutePath());
                stream = new FileInputStream(file);
            }
            clientProperties.load(stream);
        } catch (Throwable e) {
            log.warn("Could not load client properties", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return clientProperties;
    }

    public static String getPropertyStringValue(String name, Properties clientProperties, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            value = clientProperties.getProperty(name);
            if (value == null) {
                if (defaultValue == null) {
                    log.error("Could not find required property: {}", name);
                    throw new IllegalStateException(name + " property is null");
                } else {
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    public static Integer getPropertyIntValue(String name, Properties clientProperties, Integer defaultValue) {
        return NumberUtils.toInt(getPropertyStringValue(name, clientProperties, ""), defaultValue);
    }

    public static Long getPropertyLongValue(String name, Properties clientProperties, Long defaultValue) {
        return NumberUtils.toLong(getPropertyStringValue(name, clientProperties, ""), defaultValue);
    }

    public static Boolean getPropertyBoolValue(String name, Properties clientProperties, Boolean defaultValue) {
        String value = getPropertyStringValue(name, clientProperties, "");
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        return BooleanUtils.toBoolean(value);
    }

}
