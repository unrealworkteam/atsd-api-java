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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to simplify client configuration. Recommend to use Spring IOC features instead of it.
 *
 * @author Nikolay Malevanny.
 */
public class ClientConfigurationFactory {
    private static final Logger log = LoggerFactory.getLogger(ClientConfigurationFactory.class);

    private static final String DEFAULT_PROTOCOL = "http";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = ClientConfiguration.DEFAULT_TIMEOUT_MS;
    private static final int DEFAULT_READ_TIMEOUT_MS = ClientConfiguration.DEFAULT_TIMEOUT_MS;
    private static final long DEFAULT_PING_TIMEOUT_MS = ClientConfiguration.DEFAULT_PING_TIMEOUT_MS;
    private static final String DEFAULT_CLIENT_PROPERTIES_FILE_NAME = "classpath:/client.properties";
    private static final String AXIBASE_TSD_API_DOMAIN = "axibase.tsd.api";
    private static final String DEFAULT_API_PATH = "/api/v1";
    private static final String CLASSPATH_PREFIX = "classpath:";

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
    boolean ignoreSSLErrors;

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
        log.debug("Load client properties from file: {}", clientPropertiesFileName);
        Properties clientProperties = new Properties();
        try {
            InputStream stream = null;
            if (clientPropertiesFileName.startsWith(CLASSPATH_PREFIX)) {
                String resourcePath = clientPropertiesFileName.split(CLASSPATH_PREFIX)[1];
                log.info("Load properties from classpath: {}", resourcePath);
                stream = ClientConfigurationFactory.class.getResourceAsStream(resourcePath);
            } else {
                File file = new File(clientPropertiesFileName);
                log.info("Load properties from file: {}", file.getAbsolutePath());
                stream = new FileInputStream(file);
            }
            clientProperties.load(stream);
            IOUtils.closeQuietly(stream);
        } catch (Throwable e) {
            log.warn("Could not load client properties", e);
        }
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory();
        configurationFactory.serverName = load(AXIBASE_TSD_API_DOMAIN + ".server.name", clientProperties, null);
        configurationFactory.serverPort = load(AXIBASE_TSD_API_DOMAIN + ".server.port", clientProperties, null);
        configurationFactory.username = load(AXIBASE_TSD_API_DOMAIN + ".username", clientProperties, null);
        configurationFactory.password = load(AXIBASE_TSD_API_DOMAIN + ".password", clientProperties, null);
        configurationFactory.metadataPath = load(AXIBASE_TSD_API_DOMAIN + ".metadata.path"
                , clientProperties, DEFAULT_API_PATH);
        configurationFactory.dataPath = load(AXIBASE_TSD_API_DOMAIN + ".data.path"
                , clientProperties, DEFAULT_API_PATH);
        configurationFactory.protocol = load(AXIBASE_TSD_API_DOMAIN + ".protocol"
                , clientProperties, DEFAULT_PROTOCOL);
        configurationFactory.connectTimeoutMillis = loadInt(AXIBASE_TSD_API_DOMAIN + ".connection.timeout"
                , clientProperties, DEFAULT_CONNECT_TIMEOUT_MS);
        configurationFactory.readTimeoutMillis = loadInt(AXIBASE_TSD_API_DOMAIN + ".read.timeout"
                , clientProperties, DEFAULT_READ_TIMEOUT_MS);
        configurationFactory.pingTimeoutMillis = loadLong(AXIBASE_TSD_API_DOMAIN + ".ping.timeout"
                , clientProperties, DEFAULT_PING_TIMEOUT_MS);
        configurationFactory.ignoreSSLErrors = "true".equals(load(AXIBASE_TSD_API_DOMAIN + ".ssl.errors.ignore"
                , clientProperties, "false").toLowerCase().trim());
        return configurationFactory;
    }

    public ClientConfigurationFactory(String protocol, String serverName, String serverPort,
                                      String metadataPath, String dataPath, String username, String password,
                                      int connectTimeoutMillis,
                                      int readTimeoutMillis,
                                      long pingTimeoutMillis,
                                      boolean ignoreSSLErrors) {
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
    }

    public ClientConfigurationFactory(String protocol, String serverName, int serverPort,
                                      String metadataPath, String dataPath, String username, String password,
                                      int connectTimeoutMillis,
                                      int readTimeoutMillis,
                                      long pingTimeoutMillis,
                                      boolean ignoreSSLErrors) {
        this(protocol, serverName, Integer.toString(serverPort), metadataPath, dataPath, username, password,
                connectTimeoutMillis, readTimeoutMillis, pingTimeoutMillis, ignoreSSLErrors);
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
        return clientConfiguration;
    }

    private String buildMetaDataUrl() {
        return protocol + "://" + serverName + ":" + serverPort + metadataPath;
    }

    private String buildTimeSeriesUrl() {
        return protocol + "://" + serverName + ":" + serverPort + dataPath;
    }

    private static int loadInt(String name, Properties clientProperties, int defaultValue) {
        return NumberUtils.toInt(load(name, clientProperties, ""), defaultValue);
    }

    private static long loadLong(String name, Properties clientProperties, long defaultValue) {
        return NumberUtils.toLong(load(name, clientProperties, ""), defaultValue);
    }

    private static String load(String name, Properties clientProperties, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            value = clientProperties.getProperty(name);
            if (value == null) {
                if (defaultValue == null) {
                    log.error("Could not find required property: {}", name);
                    throw new IllegalStateException(name + " property is null");
                } else {
                    value = defaultValue;
                }
            }
        }
        return value;
    }
}
