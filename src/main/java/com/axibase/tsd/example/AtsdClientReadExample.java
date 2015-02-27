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
package com.axibase.tsd.example;

import com.axibase.tsd.model.data.command.GetSeriesCommand;
import com.axibase.tsd.model.data.command.SimpleAggregateMatcher;
import com.axibase.tsd.model.data.series.GetSeriesResult;
import com.axibase.tsd.model.data.series.Interpolate;
import com.axibase.tsd.model.data.series.Interval;
import com.axibase.tsd.model.data.series.IntervalUnit;
import com.axibase.tsd.model.data.series.aggregate.AggregateType;
import com.axibase.tsd.model.meta.EntityAndTags;
import com.axibase.tsd.model.meta.Metric;

import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
public class AtsdClientReadExample extends AbstractAtsdClientExample {

    public static void main(String[] args) {
        AtsdClientReadExample atsdClientReadExample = new AtsdClientReadExample();
        atsdClientReadExample.configure();
//        atsdClientExample.printMetaDataAndData("jvm_memory_used_percent");
        atsdClientReadExample.printMetaDataAndData("disk_used_percent");
    }

    private void printMetaDataAndData(String metricExample) {
        Metric metric = metaDataService.retrieveMetric(metricExample);
        if (metric == null) {
            System.out.println("Unknown metric: " + metricExample);
            return;
        }
        List<EntityAndTags> entityAndTagsList = metaDataService.retrieveEntityAndTags(metric.getName(), null);
        System.out.println("===Metric MetaData===");
        System.out.println("Metric: " + metric.getName());
        for (EntityAndTags entityAndTags : entityAndTagsList) {
            String entityName = entityAndTags.getEntityName();
            System.out.println("\n===Entity MetaData===");
            System.out.println("Entity: " + entityName);
            Map<String, String> tags = entityAndTags.getTags();
            System.out.println("===Tags===");
            for (Map.Entry<String, String> tagAndValue : tags.entrySet()) {
                System.out.println("\t" + tagAndValue.getKey() + " : " + tagAndValue.getValue());
            }

            System.out.println("===Series===");
            GetSeriesCommand command = new GetSeriesCommand(entityName, metric.getName(), tags);
            command.setAggregateMatcher(new SimpleAggregateMatcher(new Interval(1, IntervalUnit.MINUTE),
                    Interpolate.NONE,
                    AggregateType.DETAIL));
            List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(command);
            for (GetSeriesResult getSeriesResult : getSeriesResults) {
                print(getSeriesResult);
            }
        }
    }


}
