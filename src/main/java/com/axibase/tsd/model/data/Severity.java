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
package com.axibase.tsd.model.data;

/**
 * @author Alexander Tokarev.
 */
public enum Severity {
    UNDEFINED(0, "undefined"),
    UNKNOWN(1, "unknown"),
    NORMAL(2, "normal"),
    WARNING(3, "warning"),
    MINOR(4, "minor"),
    MAJOR(5, "major"),
    CRITICAL(6, "critical"),
    FATAL(7, "fatal");

    private final int id;
    private final String label;

    private Severity(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getCode() {
        return String.valueOf(id);
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
