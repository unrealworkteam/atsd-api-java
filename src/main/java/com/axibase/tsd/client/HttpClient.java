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
import com.axibase.tsd.model.system.ServerError;
import com.axibase.tsd.query.QueryPart;
import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.axibase.tsd.util.AtsdUtil.JSON;

/**
 * @author Nikolay Malevanny.
 */
class HttpClient {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    private final static java.util.logging.Logger legacyLogger = java.util.logging.Logger.getLogger(HttpClient.class.getName());
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_FAIL = 400;

    private ClientConfiguration clientConfiguration;
    private final Client client;

    HttpClient(ClientConfiguration clientConfiguration) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig
                .register(JsonMappingExceptionMapper.class)
                .register(JsonParseExceptionMapper.class)
                .register(JacksonJaxbJsonProvider.class, new Class[]{MessageBodyReader.class, MessageBodyWriter.class})
                .register(RequestBodyLogger.class)
                .register(HttpAuthenticationFeature.basic(clientConfiguration.getUserName(), clientConfiguration.getPassword()))
        ;

        ConnectorProvider connectorProvider = new ApacheConnectorProvider();
        clientConfig.connectorProvider(connectorProvider);


        if (log.isDebugEnabled()) {
            clientConfig.register(new LoggingFilter(legacyLogger, true));
        }

        client = ClientBuilder.newBuilder().withConfig(clientConfig).build();

        client.property(ClientProperties.CONNECT_TIMEOUT, 3000);
        client.property(ClientProperties.READ_TIMEOUT,    3000);

        this.clientConfiguration = clientConfiguration;
    }

    <T> List<T> requestMetaDataList(Class<T> clazz, QueryPart<T> query) {
        return requestList(clientConfiguration.getMetaDataUrl(), clazz, query, null);
    }

    public <T> T requestMetaDataObject(Class<T> clazz, QueryPart<T> query) {
        return requestObject(clientConfiguration.getMetaDataUrl(), clazz, query);
    }

    public <E> boolean updateMetaData(QueryPart query, RequestProcessor<E> requestProcessor) {
        return update(clientConfiguration.getMetaDataUrl(), query, requestProcessor);
    }

    public <E> boolean updateData(QueryPart query, RequestProcessor<E> requestProcessor) {
        return update(clientConfiguration.getDataUrl(), query, requestProcessor);
    }

    public boolean updateData(QueryPart query, String data) {
        return update(clientConfiguration.getDataUrl(), query, RequestProcessor.post(data), MediaType.TEXT_PLAIN);
    }

    public <T, E> List<T> requestDataList(Class<T> clazz, QueryPart<T> query, RequestProcessor<E> requestProcessor) {
        String url = clientConfiguration.getDataUrl();
        return requestList(url, clazz, query, requestProcessor);
    }

    private <T, E> List<T> requestList(String url, Class<T> resultClass, QueryPart<T> query, RequestProcessor<E> requestProcessor) {
        Response response = doRequest(url, query, requestProcessor);
        if (response.getStatus() == HTTP_STATUS_OK) {
            return response.readEntity(listType(resultClass));
        } else {
            throw buildException(response);
        }
    }

    private <T> T requestObject(String url, Class<T> resultClass, QueryPart<T> query) {
        Response response = doRequest(url, query, null);
        if (response.getStatus() == HTTP_STATUS_OK) {
            return response.readEntity(resultClass);
        } else {
            throw buildException(response);
        }
    }

    private <E> boolean update(String url, QueryPart query, RequestProcessor<E> requestProcessor) {
        Response response = doRequest(url, query, requestProcessor);
        if (response.getStatus() == HTTP_STATUS_OK) {
            return true;
        } else if (response.getStatus() == HTTP_STATUS_FAIL) {
            return false;
        } else {
            throw buildException(response);
        }
    }

    private <E> boolean update(String url, QueryPart query, RequestProcessor<E> requestProcessor, String mediaType) {
        Response response = doRequest(url, query, requestProcessor, mediaType);
        if (response.getStatus() == HTTP_STATUS_OK) {
            return true;
        } else if (response.getStatus() == HTTP_STATUS_FAIL) {
            return false;
        } else {
            throw buildException(response);
        }
    }

    private AtsdServerException buildException(Response response) {
        ServerError serverError = null;
        try {
            if (response.getHeaderString("Content-Type").startsWith(JSON))
            serverError = response.readEntity(ServerError.class);
            log.warn("Server error: {}", serverError);
        } catch (Throwable e) {
            log.warn("Couldn't read error message", e);
        }
        return new AtsdServerException(response.getStatusInfo().getReasonPhrase() + " (" + response.getStatus() + ")" +
                ((serverError == null) ? "" : (", " + serverError.getMessage()))
        );
    }

    private <T, E> Response doRequest(String url, QueryPart<T> query, RequestProcessor<E> requestProcessor) {
        return doRequest(url, query, requestProcessor, JSON);
    }

    private <T, E> Response doRequest(String url, QueryPart<T> query, RequestProcessor<E> requestProcessor, String mediaType) {
        WebTarget target = client.target(url);
        target = query.fill(target);
        log.info("url = {}", target.getUri());
        Invocation.Builder request = target.request(mediaType);

        Response response = null;
        try {
            if (requestProcessor == null) {
                response = request.get();
            } else {
                response = requestProcessor.process(request, mediaType);
            }
        } catch (Throwable e) {
            throw new AtsdClientException("Error while processing the request", e);
        }
        return response;
    }

    private <T> GenericType<List<T>> listType(final Class<T> clazz) {
        ParameterizedType genericType = new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[]{clazz};
            }

            public Type getRawType() {
                return List.class;
            }

            public Type getOwnerType() {
                return List.class;
            }
        };
        return new GenericType<List<T>>(genericType) {
        };
    }

    void close() {
        if (client != null) {
            client.close();
        }
    }
}
