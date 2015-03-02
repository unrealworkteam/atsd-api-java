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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
public class GetSeriesBatchCommand {
    @JsonProperty("series")
    private final List<GetSeriesCommand> seriesCommands;

    public GetSeriesBatchCommand(List<GetSeriesCommand> seriesCommands) {
        this.seriesCommands = seriesCommands;
    }

    public List<GetSeriesCommand> getSeriesCommands() {
        return seriesCommands;
    }
}