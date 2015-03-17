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

import com.axibase.tsd.RerunRule;
import com.axibase.tsd.TestUtil;
import com.axibase.tsd.model.data.Property;
import com.axibase.tsd.model.meta.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.*;

import static com.axibase.tsd.TestUtil.*;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Nikolay Malevanny.
 */
public class MetaDataServiceTest {
    private MetaDataService metaDataService;
    private DataService dataService;
    private HttpClientManager httpClientManager;

    @Rule
    public RerunRule rerunRule = new RerunRule();

    @Before
    public void setUp() throws Exception {
        httpClientManager = TestUtil.buildHttpClientManager();
        metaDataService = new MetaDataService();
        metaDataService.setHttpClientManager(httpClientManager);
        dataService = new DataService();
        dataService.setHttpClientManager(httpClientManager);

        waitWorkingServer(httpClientManager);
    }

    @Test
    public void testRetrieveMetrics() throws Exception {
        List metrics = metaDataService.retrieveMetrics(true, "name like '*'", TagAppender.ALL, 1);
        assertTrue(metrics.get(0) instanceof Metric);
        assertEquals(1, metrics.size());
    }

    @Test
    public void testRetrieveMetric() throws Exception {
        Metric metric = metaDataService.retrieveMetric(TTT_METRIC);
        assertNotNull(metric);
        assertEquals(TTT_METRIC, metric.getName());
        assertEquals(DataType.DOUBLE, metric.getDataType());
    }

    @Test
    public void testCreateOrReplaceMetric() throws Exception {
        Metric metric = metaDataService.retrieveMetric(TTT_METRIC);
        metric.setDataType(DataType.LONG);
        metaDataService.createOrReplaceMetric(metric);

        Metric updatedMetric = metaDataService.retrieveMetric(TTT_METRIC);
        assertNotNull(updatedMetric);
        assertEquals(TTT_METRIC, updatedMetric.getName());
        assertEquals(DataType.LONG, updatedMetric.getDataType());

        updatedMetric.setDataType(DataType.DOUBLE);
        metaDataService.createOrReplaceMetric(updatedMetric);

        metric = metaDataService.retrieveMetric(TTT_METRIC);
        assertNotNull(metric);
        assertEquals(DataType.DOUBLE, metric.getDataType());
    }

    @Test
    public void testCreateAndDeleteMetric() throws Exception {
        Metric newTestMetric = createNewTestMetric();
        try {
            metaDataService.deleteMetric(newTestMetric);
        } catch (Throwable e) {
            // ignore
        }
        assertNull(metaDataService.retrieveMetric(NNN_METRIC));

        assertTrue(metaDataService.createOrReplaceMetric(newTestMetric));
        assertNotNull(metaDataService.retrieveMetric(NNN_METRIC));

        assertTrue(metaDataService.deleteMetric(newTestMetric));
        assertNull(metaDataService.retrieveMetric(NNN_METRIC));
    }

    @Test
    public void testUpdateMetric() throws Exception {
        Metric metric = metaDataService.retrieveMetric(TTT_METRIC);
        Map<String, String> oldTags = metric.getTags();

        if (oldTags != null && oldTags.containsKey(UUU_TAG)) {
            oldTags.remove(UUU_TAG);
            metaDataService.createOrReplaceMetric(metric);
        }

        Map<String, String> newTags = new HashMap<String, String>();
        newTags.put(UUU_TAG, "uuu-tag-value");
        metric.setTags(newTags);

        metaDataService.updateMetric(metric);

        metric = metaDataService.retrieveMetric(TTT_METRIC);
        assertTrue(metric.getTags().containsKey(UUU_TAG));

        metric.setTags(oldTags);
        metaDataService.createOrReplaceMetric(metric);

        metric = metaDataService.retrieveMetric(TTT_METRIC);
        assertFalse(metric.getTags().containsKey(UUU_TAG));
    }

    @Test
    public void testRetrieveEntities() throws Exception {
        {
            List<Entity> entities = metaDataService.retrieveEntities(null, "name like 'ttt-*'", TagAppender.ALL, 1);
            assertTrue(entities.get(0) instanceof Entity);
            assertEquals(1, entities.size());
        }

        {
            List<Entity> entities = metaDataService.retrieveEntities(null, "name = 'ttt-entity'", TagAppender.ALL, 1);
            assertTrue(entities.get(0) instanceof Entity);
            assertEquals(1, entities.size());
        }
    }

    @Test
    public void testRetrieveEntity() throws Exception {
        Entity entity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertEquals(TTT_ENTITY, entity.getName());
    }

    @Test
    public void testCreateOrReplaceEntity() throws Exception {
        Entity entity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertEquals(TTT_ENTITY, entity.getName());
        if (entity.getTags().containsKey("uuu-tag-1")) {
            entity.getTags().remove("uuu-tag-1");
        }

        Map<String, String> tags = entity.getTags();
        Map<String, String> savedTags = new HashMap<String, String>(tags);
        tags.put("uuu-tag-1", "uuu-tag-value-1");
        entity.setTags(tags);
        assertTrue(metaDataService.createOrReplaceEntity(entity));

        Entity updatedEntity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertEquals(TTT_ENTITY, updatedEntity.getName());
        assertTrue(updatedEntity.getTags().containsKey("uuu-tag-1"));

        updatedEntity.setTags(savedTags);
        metaDataService.createOrReplaceEntity(updatedEntity);

        entity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertEquals(TTT_ENTITY, entity.getName());
        assertFalse(entity.getTags().containsKey("uuu-tag-1"));
    }

    @Test
    public void testCreateAndDeleteEntity() throws Exception {
        if (metaDataService.retrieveEntity(NNN_ENTITY) != null) {
            metaDataService.deleteEntity(new Entity(NNN_ENTITY));
        }

        Entity entity = new Entity(NNN_ENTITY);
        entity.buildTags("nnn-test-tag-1", "nnn-test-tag-value-1");
        metaDataService.createOrReplaceEntity(entity);

        assertNotNull(metaDataService.retrieveEntity(NNN_ENTITY));

        metaDataService.deleteEntity(entity);

        assertNull(metaDataService.retrieveEntity(NNN_ENTITY));
    }

    @Test
    public void testUpdateEntity() throws Exception {
        Entity entity = metaDataService.retrieveEntity(TTT_ENTITY);
        Map<String, String> oldTags = entity.getTags();
        if (oldTags.containsKey(UUU_TAG)) {
            oldTags.remove(UUU_TAG);
            metaDataService.createOrReplaceEntity(entity);
        }

        Map<String, String> newTags = new HashMap<String, String>(oldTags);
        newTags.put(UUU_TAG, "uuu-tag-value");
        entity.setTags(newTags);

        assertTrue(metaDataService.createOrReplaceEntity(entity));

        entity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertTrue(entity.getTags().containsKey(UUU_TAG));

        entity.setTags(oldTags);
        metaDataService.createOrReplaceEntity(entity);

        entity = metaDataService.retrieveEntity(TTT_ENTITY);
        assertFalse(entity.getTags().containsKey(UUU_TAG));
    }

    @Test
    public void testRetrieveEntityGroups() throws Exception {
        List<EntityGroup> entityGroups = metaDataService.retrieveEntityGroups();
        assertTrue(entityGroups.get(0) instanceof EntityGroup);
        assertTrue(entityGroups.size() > 0);
        boolean containsTestEntityGroup = false;
        for (Iterator<EntityGroup> iterator = entityGroups.iterator(); iterator.hasNext() && !containsTestEntityGroup; ) {
            EntityGroup entityGroup = iterator.next();
            containsTestEntityGroup = TTT_ENTITY_GROUP.equals(entityGroup.getName());
        }
        assertTrue(containsTestEntityGroup);
    }

    @Test
    public void testRetrieveEntityGroup() throws Exception {
        EntityGroup entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertEquals(TTT_ENTITY_GROUP, entityGroup.getName());
    }

    @Test
    public void testCreateOrReplaceEntityGroup() throws Exception {
        EntityGroup entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertEquals(TTT_ENTITY_GROUP, entityGroup.getName());
        if (entityGroup.getTags().containsKey("uuu-tag-1")) {
            entityGroup.getTags().remove("uuu-tag-1");
            metaDataService.createOrReplaceEntityGroup(entityGroup);
        }

        Map<String, String> tags = entityGroup.getTags();
        Map<String, String> savedTags = new HashMap<String, String>(tags);
        tags.put("uuu-tag-1", "uuu-tag-value-1");
        entityGroup.setTags(tags);
        assertTrue(metaDataService.createOrReplaceEntityGroup(entityGroup));

        EntityGroup updatedEntityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertEquals(TTT_ENTITY_GROUP, updatedEntityGroup.getName());
        assertTrue(updatedEntityGroup.getTags().containsKey("uuu-tag-1"));

        updatedEntityGroup.setTags(savedTags);
        metaDataService.createOrReplaceEntityGroup(updatedEntityGroup);

        entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertEquals(TTT_ENTITY_GROUP, entityGroup.getName());
        assertFalse(entityGroup.getTags().containsKey("uuu-tag-1"));
    }

    @Test
    public void testCreateAndDeleteEntityGroup() throws Exception {
        assertNull(metaDataService.retrieveEntityGroup(NNN_ENTITY_GROUP));

        EntityGroup entityGroup = new EntityGroup(NNN_ENTITY_GROUP);
        entityGroup.setTags("nnn-test-tag-1", "nnn-test-tag-value-1");
        metaDataService.createOrReplaceEntityGroup(entityGroup);

        assertNotNull(metaDataService.retrieveEntityGroup(NNN_ENTITY_GROUP));

        metaDataService.deleteEntityGroup(entityGroup);

        assertNull(metaDataService.retrieveEntityGroup(NNN_ENTITY_GROUP));
    }

    @Test
    public void testUpdateEntityGroup() throws Exception {
        EntityGroup entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        Map<String, String> oldTags = entityGroup.getTags();
        if (oldTags.containsKey(UUU_TAG)) {
            oldTags.remove(UUU_TAG);
            metaDataService.createOrReplaceEntityGroup(entityGroup);
        }

        Map<String, String> newTags = new HashMap<String, String>(oldTags);
        newTags.put(UUU_TAG, "uuu-tag-value");
        entityGroup.setTags(newTags);

        metaDataService.updateEntityGroup(entityGroup);

        entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertTrue(entityGroup.getTags().containsKey(UUU_TAG));

        entityGroup.setTags(oldTags);
        metaDataService.createOrReplaceEntityGroup(entityGroup);

        entityGroup = metaDataService.retrieveEntityGroup(TTT_ENTITY_GROUP);
        assertFalse(entityGroup.getTags().containsKey(UUU_TAG));
    }

    @Test
    public void testRetrieveGroupEntities() throws Exception {
        assertTrue(metaDataService.deleteGroupEntities(TTT_ENTITY_GROUP
                , new Entity("java-uuu-entity")
                , new Entity("java-sss-entity")));
        List<Entity> entityList = metaDataService
                .retrieveGroupEntities(TTT_ENTITY_GROUP, null, null, TagAppender.ALL, null);
        if (entityList.size()==0) {
            entityList = fixTestDataEntityGroupEntity();
        }
        Entity entity = entityList.get(0);
        assertEquals(1, entityList.size());
        assertEquals(TTT_ENTITY, entity.getName());
    }

    @Test
    public void testManageGroupEntities() throws Exception {
        {
            List<Entity> entityList = metaDataService
                    .retrieveGroupEntities(TTT_ENTITY_GROUP, null, null, TagAppender.ALL, null);
            if (entityList.size()==0) {
                entityList = fixTestDataEntityGroupEntity();
            }
            Entity entity = entityList.get(0);
            assertEquals(1, entityList.size());
            assertEquals(TTT_ENTITY, entity.getName());
        }
        assertEquals(1, metaDataService.retrieveGroupEntities(TTT_ENTITY_GROUP).size());
        assertTrue(metaDataService.addGroupEntities(TTT_ENTITY_GROUP, true, new Entity("java-uuu-entity")));
        assertEquals(2, metaDataService.retrieveGroupEntities(TTT_ENTITY_GROUP).size());
        assertTrue(metaDataService.replaceGroupEntities(TTT_ENTITY_GROUP, true, new Entity(TTT_ENTITY), new Entity("java-sss-entity")));
        assertTrue(metaDataService.deleteGroupEntities(TTT_ENTITY_GROUP
                , new Entity("java-sss-entity")));
        assertEquals(1, metaDataService.retrieveGroupEntities(TTT_ENTITY_GROUP).size());
    }

    @Test
    public void testRetrieveEntityAndTags() throws Exception {
        List<EntityAndTags> entityAndTagsList = metaDataService.retrieveEntityAndTags(TTT_METRIC, null);
        EntityAndTags entityAndTags = entityAndTagsList.get(0);
        assertTrue(entityAndTagsList.size() > 0);
        assertEquals(TTT_ENTITY, entityAndTags.getEntityName());
        assertTrue(entityAndTags.getTags().containsKey("ttt-tag-1"));
        assertTrue(entityAndTags.getTags().size() > 0);
    }

    @Test
    public void testRetrieveEntityAndTagsByMetricAndEntity() throws Exception {
        List<EntityAndTags> entityAndTagsList = metaDataService.retrieveEntityAndTags(TTT_METRIC, TTT_ENTITY);
        EntityAndTags entityAndTags = entityAndTagsList.get(0);
        assertTrue(entityAndTagsList.size() > 0);
        assertEquals(TTT_ENTITY, entityAndTags.getEntityName());

        try {
            metaDataService.retrieveEntityAndTags(" ", " ");
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testRetrievePropertyTypes() throws Exception {
        List<Property> properties = dataService.retrieveProperties(buildPropertiesQuery());
        if (properties.size() == 0) {
            fixTestDataProperty(dataService);
        }
        Set<String> propertyTypes = metaDataService.retrievePropertyTypes(TTT_ENTITY, 0L);
        assertTrue(propertyTypes.size() > 0 );
        assertTrue(propertyTypes.contains(TTT_TYPE));
    }


    @Test
    public void testRetrieveMetricsByEntity() throws Exception {
        List metrics = metaDataService.retrieveMetrics(TTT_ENTITY, null, "name like '*'", null, 10);
        assertTrue(metrics.get(0) instanceof Metric);
        assertEquals(1, metrics.size());
    }

    @After
    public void tearDown() {
        httpClientManager.close();
    }

    private Metric createNewTestMetric() {
        Metric newMetric = new Metric();
        newMetric.setName(NNN_METRIC);
        newMetric.setDataType(DataType.INTEGER);
        newMetric.setDescription("test");
        newMetric.setEnabled(false);
        newMetric.setMaxValue(1D);
        newMetric.setMinValue(3D);
        newMetric.buildTags(
                "nnn-tag-1", "nnn-tag-value-1",
                "nnn-tag-2", "nnn-tag-value-2"
        );
        newMetric.setTimePrecision(TimePrecision.SECONDS);
        return newMetric;
    }

    private List<Entity> fixTestDataEntityGroupEntity() {
        List<Entity> entityList;
        metaDataService.addGroupEntities(TTT_ENTITY_GROUP, true, new Entity(TTT_ENTITY));
        entityList = metaDataService
                .retrieveGroupEntities(TTT_ENTITY_GROUP, null, null, TagAppender.ALL, null);
        return entityList;
    }
}
