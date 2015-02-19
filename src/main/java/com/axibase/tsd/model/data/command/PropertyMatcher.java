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
package com.axibase.tsd.model.data.command;

import com.axibase.tsd.model.data.PropertyKey;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyMatcher {
    private PropertyKey key;
    private Long createdBeforeTime;

    public PropertyMatcher() {
    }

    public PropertyMatcher(PropertyKey key, Long createdBeforeTime) {
        this.key = key;
        this.createdBeforeTime = createdBeforeTime;
    }

    public PropertyKey getKey() {
        return key;
    }

    public void setKey(PropertyKey key) {
        this.key = key;
    }

    public Long getCreatedBeforeTime() {
        return createdBeforeTime;
    }

    public void setCreatedBeforeTime(Long createdBeforeTime) {
        this.createdBeforeTime = createdBeforeTime;
    }

    @Override
    public String toString() {
        return "PropertyMatcher{" +
                "key=" + key +
                ", createdBeforeTime=" + createdBeforeTime +
                '}';
    }
}
