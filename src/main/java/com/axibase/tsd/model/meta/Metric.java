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
package com.axibase.tsd.model.meta;

import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.TimeZone;

@Data
/* Use chained setters that return this instead of void */
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metric {
    @NonNull
    private String name;
    private String label;
    private Boolean enabled;
    private DataType dataType;
    private String units;
    private TimeZone timeZone;
    private InterpolationType interpolationType;
    private TimePrecision timePrecision;
    private Boolean persistent;
    private String filter;
    private Double minValue;
    private Double maxValue;
    private InvalidAction invalidAction;
    private String description;
    private Integer retentionDays;
    /* This field can't be inserted, only queried
     * Therefore, we prohibit setting this field
     * */
    @Setter(AccessLevel.NONE)
    private String lastInsertDate;
    private Boolean versioned;
    private Map<String, String> tags;

    public Metric buildTags(String... tagNamesAndValues) {
        setTags(AtsdUtil.toMap(tagNamesAndValues));
        return this;
    }
}
