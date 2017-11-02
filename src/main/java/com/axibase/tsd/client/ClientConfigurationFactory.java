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
package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ClientConfiguration;
import com.axibase.tsd.util.AtsdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Utility class to simplify client configuration. Recommend to use Spring IOC features instead of it.
 */
public class ClientConfigurationFactory {
    private static final String DEFAULT_PROTOCOL = "http";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = ClientConfiguration.DEFAULT_TIMEOUT_MS;
    private static final int DEFAULT_READ_TIMEOUT_MS = ClientConfiguration.DEFAULT_TIMEOUT_MS;
    private static final long DEFAULT_PING_TIMEOUT_MS = ClientConfiguration.DEFAULT_PING_TIMEOUT_MS;
    private static final String DEFAULT_CLIENT_PROPERTIES_FILE_NAME = "classpath:/client.properties";
    private static final String AXIBASE_TSD_API_DOMAIN = "axibase.tsd.api";
    private static final String DEFAULT_API_PATH = "/api/v1";

    private String protocol;
    private String serverName;
    private String serverPort;
    private String metadataPath;
    private String dataPath;
    private String username;
    private String password;
    private int connectTimeoutMillis;
    private int readTimeoutMillis;
    private long pingTimeoutMillis;
    private boolean ignoreSSLErrors;
    private boolean skipStreamingControl;
    private boolean enableGzipCompression;

    private ClientConfigurationFactory() {
    }

    public static ClientConfigurationFactory createInstance() {
        String clientPropertiesFileName = DEFAULT_CLIENT_PROPERTIES_FILE_NAME;
        String sysPropertiesFileName = System.getProperty(AXIBASE_TSD_API_DOMAIN + ".client.properties");
        if (StringUtils.isNotBlank(sysPropertiesFileName)) {
            clientPropertiesFileName = sysPropertiesFileName;
        }
        return createInstance(clientPropertiesFileName);
    }

    public static ClientConfigurationFactory createInstance(String clientPropertiesFileName) {
        Properties clientProperties = AtsdUtil.loadProperties(clientPropertiesFileName);
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory();
        configurationFactory.serverName = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".server.name", clientProperties, null);
        configurationFactory.serverPort = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".server.port", clientProperties, null);
        configurationFactory.username = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".username", clientProperties, null);
        configurationFactory.password = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".password", clientProperties, null);
        configurationFactory.metadataPath = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".metadata.path", clientProperties, DEFAULT_API_PATH);
        configurationFactory.dataPath = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".data.path", clientProperties, DEFAULT_API_PATH);
        configurationFactory.protocol = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".protocol", clientProperties, DEFAULT_PROTOCOL);
        configurationFactory.connectTimeoutMillis = AtsdUtil.getPropertyIntValue(
                AXIBASE_TSD_API_DOMAIN + ".connection.timeout", clientProperties, DEFAULT_CONNECT_TIMEOUT_MS);
        configurationFactory.readTimeoutMillis = AtsdUtil.getPropertyIntValue(
                AXIBASE_TSD_API_DOMAIN + ".read.timeout", clientProperties, DEFAULT_READ_TIMEOUT_MS);
        configurationFactory.pingTimeoutMillis = AtsdUtil.getPropertyLongValue(
                AXIBASE_TSD_API_DOMAIN + ".ping.timeout", clientProperties, DEFAULT_PING_TIMEOUT_MS);
        configurationFactory.ignoreSSLErrors = AtsdUtil.getPropertyBoolValue(
                AXIBASE_TSD_API_DOMAIN + ".ssl.errors.ignore", clientProperties, false);
        configurationFactory.skipStreamingControl = AtsdUtil.getPropertyBoolValue(
                AXIBASE_TSD_API_DOMAIN + ".streaming.control.skip", clientProperties, false);
        configurationFactory.enableGzipCompression = AtsdUtil.getPropertyBoolValue(
                AXIBASE_TSD_API_DOMAIN + ".compression.gzip.enable", clientProperties, false);
        return configurationFactory;
    }

    public ClientConfigurationFactory(String protocol, String serverName, String serverPort,
                                      String metadataPath, String dataPath, String username, String password,
                                      int connectTimeoutMillis,
                                      int readTimeoutMillis,
                                      long pingTimeoutMillis,
                                      boolean ignoreSSLErrors,
                                      boolean skipStreamingControl,
                                      boolean enableGzipCompression) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.metadataPath = metadataPath;
        this.dataPath = dataPath;
        this.protocol = protocol;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.pingTimeoutMillis = pingTimeoutMillis;
        this.ignoreSSLErrors = ignoreSSLErrors;
        this.skipStreamingControl = skipStreamingControl;
        this.enableGzipCompression = enableGzipCompression;
    }

    public ClientConfigurationFactory(String protocol, String serverName, int serverPort,
                                      String metadataPath, String dataPath, String username, String password,
                                      int connectTimeoutMillis,
                                      int readTimeoutMillis,
                                      long pingTimeoutMillis,
                                      boolean ignoreSSLErrors,
                                      boolean skipStreamingControl,
                                      boolean enableGzipCompression) {
        this(protocol, serverName, Integer.toString(serverPort), metadataPath, dataPath, username, password,
                connectTimeoutMillis, readTimeoutMillis, pingTimeoutMillis, ignoreSSLErrors, skipStreamingControl, enableGzipCompression);
    }

    public ClientConfiguration createClientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration(buildMetaDataUrl(),
                buildTimeSeriesUrl(),
                username,
                password);
        clientConfiguration.setConnectTimeoutMillis(connectTimeoutMillis);
        clientConfiguration.setReadTimeoutMillis(readTimeoutMillis);
        clientConfiguration.setPingTimeoutMillis(pingTimeoutMillis);
        clientConfiguration.setIgnoreSSLErrors(ignoreSSLErrors);
        clientConfiguration.setSkipStreamingControl(skipStreamingControl);
        clientConfiguration.setEnableBatchCompression(enableGzipCompression);
        return clientConfiguration;
    }

    private String buildMetaDataUrl() {
        return protocol + "://" + serverName + ":" + serverPort + metadataPath;
    }

    private String buildTimeSeriesUrl() {
        return protocol + "://" + serverName + ":" + serverPort + dataPath;
    }
}
