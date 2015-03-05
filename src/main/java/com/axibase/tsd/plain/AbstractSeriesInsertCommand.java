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

package com.axibase.tsd.plain;

import java.util.Collections;
import java.util.Map;

import static com.axibase.tsd.util.AtsdUtil.checkEntityName;

/**
 * @author Nikolay Malevanny.
 */
public abstract class AbstractSeriesInsertCommand implements PlainCommand {
    protected final String entityName;
    private final long timeMillis;
    protected final Map<String, String> tags;

    public AbstractSeriesInsertCommand(String entityName, long timeMillis, Map<String, String> tags) {
        checkEntityName(entityName);
        this.entityName = entityName;
        this.timeMillis = timeMillis;
        this.tags = tags == null ? Collections.<String, String>emptyMap() : tags;
    }

    @Override
    public final String compose() {
        StringBuilder sb = new StringBuilder("series ")
                .append("e:").append(entityName).append(' ')
                .append("ms:").append(timeMillis).append(' ')
                ;
        for (Map.Entry<String, String> tagNameAndValue : tags.entrySet()) {
            sb.append(" t:").append(tagNameAndValue.getKey()).append('=').append(tagNameAndValue.getValue());
        }
        addValues(sb);
        return sb.append('\n').toString();
    }

    protected abstract void addValues(StringBuilder sb);
}
