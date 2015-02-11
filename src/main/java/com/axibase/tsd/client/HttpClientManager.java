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
import com.axibase.tsd.model.system.RequestBodyBuilder;
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
    private ClientConfiguration clientConfiguration;
    private GenericObjectPoolConfig objectPoolConfig = new GenericObjectPoolConfig();

    private AtomicReference<GenericObjectPool<HttpClient>> objectPoolAtomicReference = new AtomicReference<GenericObjectPool<HttpClient>>();
    private int borrowMaxWaitMillis = 1000;

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
            objectPoolAtomicReference.get().returnObject(httpClient);
        }
    }

    public <T> T requestMetaDataObject(Class<T> clazz, QueryPart<T> query) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.requestMetaDataObject(clazz, query);
        } finally {
            objectPoolAtomicReference.get().returnObject(httpClient);
        }
    }



    public <T,E> List<T> requestDataList(Class<T> clazz, QueryPart<T> query, RequestBodyBuilder<E> requestBodyBuilder) {
        HttpClient httpClient = borrowClient();
        try {
            return httpClient.requestDataList(clazz, query, requestBodyBuilder);
        } finally {
            objectPoolAtomicReference.get().returnObject(httpClient);
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
