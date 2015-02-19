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
package com.axibase.tsd.model.system;

/**
 * Contains client configuration parameters.
 *
 * @author Nikolay Malevanny.
 */
public class ClientConfiguration {
    public static final int DEFAULT_TIMEOUT = 1000;

    private final String metadataUrl;
    private final String dataUrl;
    private final String username;
    private final String password;
    private int connectTimeout = DEFAULT_TIMEOUT;
    private int readTimeout = DEFAULT_TIMEOUT;

    /**
     * @param metadataUrl full URL to Metadata ATSD API
     * @param dataUrl full URL to Data ATSD API
     * @param username user name to login
     * @param password password to login
     */
    public ClientConfiguration(String metadataUrl, String dataUrl, String username, String password) {
        this.metadataUrl = metadataUrl;
        this.dataUrl = dataUrl;
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

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
