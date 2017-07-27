/*
 * Copyright 2017 Axibase Corporation or its affiliates. All Rights Reserved.
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
package com.axibase.tsd.model.data.series;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {
    public BigDecimalDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(com.fasterxml.jackson.core.JsonParser parser,
                                  DeserializationContext ctxt) throws IOException {
        String stringRepresentation = parser.getValueAsString();
        if ("NaN".equalsIgnoreCase(stringRepresentation)) {
            return null;
        }
        return parser.getDecimalValue();
    }
}
