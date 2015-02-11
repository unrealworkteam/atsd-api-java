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
 * @author Nikolay Malevanny.
 */
public class ClientConfiguration {
    public static final int DEFAULT_TIMEOUT = 1000;

    private final String metaDataUrl;
    private final String dataUrl;
    private final String userName;
    private final String password;
    private int connectTimeout = DEFAULT_TIMEOUT;
    private int readTimeout = DEFAULT_TIMEOUT;

    public ClientConfiguration(String metaDataUrl, String timeSeriesUrl, String userName, String password) {
        this.metaDataUrl = metaDataUrl;
        this.dataUrl = timeSeriesUrl;
        this.userName = userName;
        this.password = password;
    }

    public String getMetaDataUrl() {
        return metaDataUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getUserName() {
        return userName;
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
