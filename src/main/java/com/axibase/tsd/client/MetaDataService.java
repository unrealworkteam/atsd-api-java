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
package com.axibase.tsd.client;

import com.axibase.tsd.model.meta.*;
import com.axibase.tsd.query.Query;
import com.axibase.tsd.query.QueryPart;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
public class MetaDataService {
    private HttpClientManager httpClientManager;

    public void setHttpClientManager(HttpClientManager httpClientManager) {
        this.httpClientManager = httpClientManager;
    }

    /**
     * @param active      Filter metrics by {@code lastInsertTime}. If active = {@code true}, only metrics with
     *                    positive {@code lastInsertTime} are included in the response.
     * @param expression  Specify EL expression.
     * @param tagAppender Specify metric tags to be included in the response.
     * @param limit       Limit response to first N metrics, ordered by name.
     * @return List of metrics.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<Metric> retrieveMetrics(Boolean active,
                                        String expression,
                                        TagAppender tagAppender,
                                        Integer limit) throws AtsdClientException, AtsdServerException {
        QueryPart<Metric> query = new Query<Metric>("metrics")
                .param("active", active)
                .param("expression", expression)
                .param("tags", tagAppender.getTags())
                .param("limit", limit);
        return httpClientManager.requestMetaDataList(Metric.class, query);
    }

    /**
     * @param entityName  Entity name.
     * @param active      Filter metrics by {@code lastInsertTime}. If active = {@code true}, only metrics with
     *                    positive {@code lastInsertTime} are included in the response.
     * @param expression  Specify EL expression.
     * @param tagAppender Specify metric tags to be included in the response.
     * @param limit       Limit response to first N metrics, ordered by name.
     * @return List of metrics.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<Metric> retrieveMetrics(String entityName,
                                        Boolean active,
                                        String expression,
                                        TagAppender tagAppender,
                                        Integer limit) throws AtsdClientException, AtsdServerException {
        QueryPart<Metric> query = new Query<Metric>("entities")
                .path(entityName)
                .path("metrics")
                .param("active", active)
                .param("expression", expression)
                .param("tags", tagAppender.getTags())
                .param("limit", limit);
        return httpClientManager.requestMetaDataList(Metric.class, query);
    }

    /**
     * @param metricName Metric name.
     * @return Metric.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public Metric retrieveMetric(String metricName)
            throws AtsdClientException, AtsdServerException {
        check(metricName, "Metric name is empty");
        return httpClientManager.requestMetaDataObject(Metric.class, new Query<Metric>("metrics")
                .path(metricName));
    }

    /**
     * @param active      Filter entities by {@code lastInsertTime}. If active = {@code true}, only entities with
     *                    positive {@code lastInsertTime} are included in the response.
     * @param expression  Specify EL expression.
     * @param tagAppender Specify entity tags to be included in the response.
     * @param limit       Limit response to first N entities, ordered by name.
     * @return List of entities.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<Entity> retrieveEntities(Boolean active,
                                         String expression,
                                         TagAppender tagAppender,
                                         Integer limit) throws AtsdClientException, AtsdServerException {
        QueryPart<Entity> query = new Query<Entity>("entities")
                .param("active", active)
                .param("expression", expression)
                .param("tags", tagAppender.getTags())
                .param("limit", limit);
        return httpClientManager.requestMetaDataList(Entity.class, query);
    }

    /**
     * @param entityName Entity name.
     * @return Entity
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public Entity retrieveEntity(String entityName)
            throws AtsdClientException, AtsdServerException {
        check(entityName, "Entity name is empty");
        QueryPart<Entity> query = new Query<Entity>("entities")
                .path(entityName);
        return httpClientManager.requestMetaDataObject(Entity.class, query);
    }

    /**
     * @param metricName Metric name.
     * @param entityName Entity name.
     * @return List of entities and tags for metric.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<EntityAndTags> retrieveEntityAndTags(String metricName, String entityName)
            throws AtsdClientException, AtsdServerException {
        check(metricName, "Metric name is empty");
        return httpClientManager.requestMetaDataList(EntityAndTags.class, new Query<EntityAndTags>("metrics")
                        .path(metricName)
                        .path("entity-and-tags")
                        .param("entity", entityName)
        );
    }

    /**
     * @return List of entity groups.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<EntityGroup> retrieveEntityGroups() throws AtsdClientException, AtsdServerException {
        return httpClientManager.requestMetaDataList(EntityGroup.class, new Query<EntityGroup>("entity-groups"));
    }

    /**
     * @param entityGroupName Entity group name.
     * @return List of entity groups.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public EntityGroup retrieveEntityGroup(String entityGroupName) throws AtsdClientException, AtsdServerException {
        QueryPart<EntityGroup> query = new Query<EntityGroup>("entity-groups")
                .path(entityGroupName);
        return httpClientManager.requestMetaDataObject(EntityGroup.class, query);
    }

    /**
     * @param entityGroupName Entity group name.
     * @param active          Filter entities by {@code lastInsertTime}. If active = {@code true}, only entities with
     *                        positive {@code lastInsertTime} are included in the response.
     * @param expression      Specify EL expression.
     * @param tagAppender     Specify entity tags to be included in the response.
     * @return List of entities (short info) for an entity group.
     * @throws AtsdClientException
     * @throws AtsdServerException
     */
    public List<Entity> retrieveGroupEntities(String entityGroupName,
                                              Boolean active,
                                              String expression,
                                              TagAppender tagAppender,
                                              Integer limit) throws AtsdClientException, AtsdServerException {
        check(entityGroupName, "Entity group name is empty");
        QueryPart<Entity> query = new Query<Entity>("entity-groups")
                .path(entityGroupName)
                .path("entities")
                .param("active", active)
                .param("expression", expression)
                .param("tags", tagAppender.getTags())
                .param("limit", limit);
        return httpClientManager.requestMetaDataList(Entity.class, query);
    }


    private void check(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
