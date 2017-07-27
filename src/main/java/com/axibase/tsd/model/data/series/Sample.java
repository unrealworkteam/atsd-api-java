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
package com.axibase.tsd.model.data.series;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
/* Use chained setters that return this instead of void */
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sample {
    @JsonProperty("t")
    private Long timeMillis;
    @JsonProperty("d")
    private String date;
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("v")
    private BigDecimal numericValue;
    @JsonProperty("x")
    private String textValue;

    @JsonCreator
    public Sample() {}

    public Sample(long timeMillis, double numericValue, String textValue) {
        setTimeMillis(timeMillis);
        setNumericValueFromDouble(numericValue);
        this.textValue = textValue;
    }

    public Sample(long timeMillis, double value) {
        this(timeMillis, value, null);
    }

    @JsonIgnore
    public double getNumericValueAsDouble() {
        return numericValue == null ? Double.NaN : numericValue.doubleValue();
    }

    @JsonIgnore
    public Sample setNumericValueFromDouble(double numericValue) {
        if (Double.isNaN(numericValue) || Double.isInfinite(numericValue)) {
            this.numericValue = null;
        } else {
            this.numericValue = new BigDecimal(numericValue);
        }

        return this;
    }

    public Sample setTimeMillis(Long timeMillis) {
        this.date = null;
        this.timeMillis = timeMillis;

        return this;
    }

    public Sample setDate(String date) {
        this.timeMillis = null;
        this.date = date;

        return this;
    }
}
