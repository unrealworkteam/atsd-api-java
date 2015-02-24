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
package com.axibase.tsd;

import com.axibase.tsd.client.ClientConfigurationFactory;
import com.axibase.tsd.client.HttpClientManager;
import com.axibase.tsd.model.system.ClientConfiguration;
import com.axibase.tsd.util.AtsdUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Nikolay Malevanny.
 */
public class TestUtil {
    public static final String TTT_TYPE = "ttt-type";
    public static final String NNN_TYPE = "java-nnn-type";
    public static final String TTT_ENTITY = "ttt-entity";
    public static final String NNN_ENTITY = "nnn-entity";
    public static final String TTT_METRIC = "ttt-metric";
    public static final String NNN_METRIC = "java-nnn-metric";
    public static final String TTT_ENTITY_GROUP = "ttt-entity-group";
    public static final String NNN_ENTITY_GROUP = "nnn-entity-group";

    // To overwrite client properties use Maven properties like:
    // -DargLine="-Daxibase.tsd.api.server.name=10.100.10.5 -Daxibase.tsd.api.server.port=8888"
    public static HttpClientManager buildHttpClientManager() {
        // Use -Daxibase.tsd.api.client.properties=<filename> to change default properties file name
        ClientConfigurationFactory configurationFactory = ClientConfigurationFactory.createInstance();
        ClientConfiguration clientConfiguration = configurationFactory.createClientConfiguration();
        HttpClientManager httpClientManager = new HttpClientManager();
        httpClientManager.setClientConfiguration(clientConfiguration);
        GenericObjectPoolConfig objectPoolConfig = new GenericObjectPoolConfig();
        objectPoolConfig.setMaxTotal(100);
        objectPoolConfig.setMaxIdle(100);
        httpClientManager.setObjectPoolConfig(objectPoolConfig);
        httpClientManager.setBorrowMaxWaitMillis(10000);
        return httpClientManager;
    }

    public static MultivaluedMap<String,String> toMVM(String... tagNamesAndValues) {
        return new MultivaluedHashMap<String, String>(AtsdUtil.toMap(tagNamesAndValues));
    }
}
