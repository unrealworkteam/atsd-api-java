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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TimeZone;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity {
    private String name;
    private String label;
    private Boolean enabled;
    private Interpolate interpolate;
    private TimeZone timeZone;
    private String lastInsertDate;
    private Map<String, String> tags;

    public Entity() {
    }

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Entity setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Entity setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;

    }

    public String getLastInsertDate() {
        return lastInsertDate;
    }

    public Interpolate getInterpolate() {
        return interpolate;
    }

    public void setInterpolate(Interpolate interpolate) {
        this.interpolate = interpolate;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Entity setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;

    }

    public Entity buildTags(String... tagNamesAndValues) {
        setTags(AtsdUtil.toMap(tagNamesAndValues));
        return this;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", enabled=" + enabled +
                ", interpolate=" + interpolate +
                ", timeZone=" + timeZone +
                ", lastInsertDate='" + lastInsertDate + '\'' +
                ", tags=" + tags +
                '}';
    }
}
