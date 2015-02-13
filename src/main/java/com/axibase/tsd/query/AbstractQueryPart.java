package com.axibase.tsd.query;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nikolay Malevanny.
 */
public abstract class AbstractQueryPart<T> implements QueryPart<T> {
    public QueryPart<T> param(String name, Object value) {
        if (value != null) {
            return new QueryParam<T>(name, value, this);
        } else {
            return this;
        }
    }

    public QueryPart<T> path(String path) {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Path element is empty");
        }
        return new Query<T>(path, this);
    }
}
