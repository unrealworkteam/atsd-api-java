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

import com.axibase.tsd.model.system.ClientConfiguration;
import com.axibase.tsd.query.QueryPart;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Nikolay Malevanny.
 */
public class HttpClientManager {
    public static final int DEFAULT_BORROW_MAX_TIME_MS = 3000;
    public static final int DEFAULT_MAX_TOTAL = 100;
    public static final int DEFAULT_MAX_IDLE = 100;

    private ClientConfiguration clientConfiguration;
    private GenericObjectPoolConfig objectPoolConfig;

    private AtomicReference<GenericObjectPool<HttpClient>> objectPoolAtomicReference = new AtomicReference<GenericObjectPool<HttpClient>>();
    private int borrowMaxWaitMillis = DEFAULT_BORROW_MAX_TIME_MS;

    public HttpClientManager() {
        objectPoolConfig = new GenericObjectPoolConfig();
        objectPoolConfig.setMaxTotal(DEFAULT_MAX_TOTAL);
        objectPoolConfig.setMaxIdle(DEFAULT_MAX_IDLE);
    }

    public HttpClientManager(ClientConfiguration clientConfiguration) {
        this();
        this.clientConfiguration = clientConfiguration;
    }

    public void setClientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    public void setObjectPoolConfig(GenericObjectPoolConfig objectPoolConfig) {
        this.objectPoolConfig = objectPoolConfig;
    }

    public void setBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
        this.borrowMaxWaitMillis = borrowMaxWaitMillis;
    }

    public <T> List<T> requestMetaDataList(Class<T> clazz, QueryPart<T> query) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.requestMetaDataList(clazz, query);
        } finally {
            returnClient(httpClient);
        }
    }

    public <T> T requestMetaDataObject(Class<T> clazz, QueryPart<T> query) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.requestMetaDataObject(clazz, query);
        } finally {
            returnClient(httpClient);
        }
    }

     public <E> boolean updateMetaData(QueryPart query, RequestProcessor<E> requestProcessor) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.updateMetaData(query, requestProcessor);
        } finally {
            returnClient(httpClient);
        }
    }

    public boolean updateData(QueryPart query, RequestProcessor requestProcessor) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.updateData(query, requestProcessor);
        } finally {
            returnClient(httpClient);
        }
    }

    public boolean updateData(QueryPart query, String data) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.updateData(query, data);
        } finally {
            returnClient(httpClient);
        }
    }

    public <T,E> List<T> requestDataList(Class<T> clazz, QueryPart<T> query, RequestProcessor<E> requestProcessor) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.requestDataList(clazz, query, requestProcessor);
        } finally {
            returnClient(httpClient);
        }
    }

    private HttpClient borrowClient() {
        GenericObjectPool<HttpClient> objectPool = createObjectPool();
        HttpClient httpClient;
        try {
            borrowMaxWaitMillis = 1000;
            httpClient = objectPool.borrowObject(borrowMaxWaitMillis);
        } catch (Exception e) {
            throw new AtsdClientException("Could not borrow http client from pool", e);
        }
        return httpClient;
    }

    private void returnClient(HttpClient httpClient) {
        objectPoolAtomicReference.get().returnObject(httpClient);
    }

    private GenericObjectPool<HttpClient> createObjectPool() {
        GenericObjectPool<HttpClient> httpClientGenericObjectPool = objectPoolAtomicReference.get();
        if (httpClientGenericObjectPool == null) {
            httpClientGenericObjectPool = new GenericObjectPool<HttpClient>(new HttpClientBasePooledObjectFactory(), objectPoolConfig);
            objectPoolAtomicReference.compareAndSet(null, httpClientGenericObjectPool);
        }
        return objectPoolAtomicReference.get();
    }

    public void close() {
        GenericObjectPool<HttpClient> pool = objectPoolAtomicReference.get();
        if (pool != null) {
            pool.close();
        }
    }

    private class HttpClientBasePooledObjectFactory extends BasePooledObjectFactory<HttpClient> {
        @Override
        public HttpClient create() throws Exception {
            return new HttpClient(clientConfiguration);
        }

        @Override
        public PooledObject<HttpClient> wrap(HttpClient httpClient) {
            return new DefaultPooledObject<HttpClient>(httpClient);
        }

        @Override
        public void destroyObject(PooledObject<HttpClient> p) throws Exception {
            p.getObject().close();
        }
    }
}
