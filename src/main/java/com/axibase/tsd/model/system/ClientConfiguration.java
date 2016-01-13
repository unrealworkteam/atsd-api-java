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
package com.axibase.tsd.model.system;

/**
 * Contains client configuration parameters.
 *
 * @author Nikolay Malevanny.
 */
public class ClientConfiguration {
    public static final int DEFAULT_TIMEOUT_MS = 1000;
    public static final long DEFAULT_PING_TIMEOUT_MS = 600000L;

    private final String metadataUrl;
    private final String dataUrl;
    private final String username;
    private final String password;
    private int connectTimeoutMillis = DEFAULT_TIMEOUT_MS;
    private int readTimeoutMillis = DEFAULT_TIMEOUT_MS;
    private boolean ignoreSSLErrors = false;
    private long pingTimeoutMillis = DEFAULT_PING_TIMEOUT_MS;
    private boolean skipStreamingControl = false;

    /**
     * @param metadataUrl full URL to Metadata ATSD API
     * @param dataUrl     full URL to Data ATSD API
     * @param username    user name to login
     * @param password    password to login
     */
    public ClientConfiguration(String metadataUrl, String dataUrl, String username, String password) {
        this.metadataUrl = metadataUrl;
        this.dataUrl = dataUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * @param url      full URL to both Metadata and Data ATSD API
     * @param username user name to login
     * @param password password to login
     */
    public ClientConfiguration(String url, String username, String password) {
        this.metadataUrl = url;
        this.dataUrl = url;
        this.username = username;
        this.password = password;
    }

    public String getMetadataUrl() {
        return metadataUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public boolean isIgnoreSSLErrors() {
        return ignoreSSLErrors;
    }

    public boolean isSkipStreamingControl() {
        return skipStreamingControl;
    }

    public long getPingTimeoutMillis() {
        return pingTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public void setIgnoreSSLErrors(boolean ignoreSSLErrors) {
        this.ignoreSSLErrors = ignoreSSLErrors;
    }

    public void setPingTimeoutMillis(long pingTimeoutMillis) {
        this.pingTimeoutMillis = pingTimeoutMillis;
    }

    public void setSkipStreamingControl(boolean skipStreamingControl) {
        this.skipStreamingControl = skipStreamingControl;
    }
}
