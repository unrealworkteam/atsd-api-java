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

import com.axibase.tsd.model.data.PropertyParameter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPropertiesCommand {
    @JsonProperty
    private long startTime;
    @JsonProperty
    private long endTime;
    @JsonProperty
    private boolean last;
    @JsonProperty
    private List<PropertyParameter> params;

    public GetPropertiesCommand() {
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public List<PropertyParameter> getParams() {
        return params;
    }

    public void setParams(List<PropertyParameter> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "GetPropertiesCommand{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", last=" + last +
                ", params=" + params +
                '}';
    }
}
