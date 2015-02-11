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
package com.axibase.tsd.query;

import javax.ws.rs.client.WebTarget;

/**
 * @author Nikolay Malevanny.
 */
public class QueryParam<T> implements QueryPart<T> {
    private final String name;
    private final Object value;
    private final QueryPart previous;

    QueryParam (String name, Object value, QueryPart<T> previous) {
        if (previous == null) {
            throw new IllegalArgumentException("previous is null");
        }
        this.name = name;
        this.value = value;
        this.previous = previous;
    }

    @Override
    public WebTarget fill(WebTarget target) {
        Object paramValue = value;
        if (value instanceof ParamValue) {
            paramValue = ((ParamValue ) value).toParamValue();
        }
        return (previous.fill(target)).queryParam(name, paramValue);
    }

    public QueryPart<T> param(String name, Object value) {
        return new QueryParam<T>(name, value, this);
    }

    @Override
    public QueryPart<T> path(String path) {
        return new Query<T>(path, this);
    }
}
