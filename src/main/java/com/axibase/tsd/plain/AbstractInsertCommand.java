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

import com.axibase.tsd.util.AtsdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

import static com.axibase.tsd.util.AtsdUtil.checkEntityName;

/**
 * @author Nikolay Malevanny.
 */
public abstract class AbstractInsertCommand implements PlainCommand {
    protected final String entityName;
    private final Long timeMillis;
    protected final Map<String, String> tags;

    public AbstractInsertCommand(String entityName, Long timeMillis, Map<String, String> tags) {
        checkEntityName(entityName);
        this.entityName = entityName;
        this.timeMillis = timeMillis;
        this.tags = tags == null ? Collections.<String, String>emptyMap() : tags;
    }

    @Override
    public final String compose() {
        StringBuilder sb = new StringBuilder("series")
                .append(' ').append("e:").append(clean(entityName));
        if (timeMillis!=null) {
            sb.append(' ').append("ms:").append(timeMillis);
        }
        String prefix = " t:";
        appendKeysAndValues(sb, prefix, tags);
        appendValues(sb);
        return sb.append('\n').toString();
    }

    protected static void appendKeysAndValues(StringBuilder sb, String prefix, Map<String, String> map) {
        for (Map.Entry<String, String> tagNameAndValue : map.entrySet()) {
            sb.append(prefix).append(clean(tagNameAndValue.getKey()))
                    .append('=').append(normalize(tagNameAndValue.getValue()));
        }
    }

    protected abstract void appendValues(StringBuilder sb);

    protected static String normalize(String value) {
        if (value.contains(" ")) {
            return "\""+value+"\"";
        } else {
            return value;
        }
    }

    protected static String clean(String value) {
        AtsdUtil.check(value, "Value is empty: " + value);
        return StringUtils.replacePattern(value.trim(), "[\\s\'\"]", "_");
    }
}
