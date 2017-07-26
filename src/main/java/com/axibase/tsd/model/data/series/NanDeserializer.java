package com.axibase.tsd.model.data.series;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class NanDeserializer extends StdDeserializer<BigDecimal> {
    public NanDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(com.fasterxml.jackson.core.JsonParser p,
                                  DeserializationContext ctxt) throws IOException {
        String stringRepresentation = p.getValueAsString();
        if (stringRepresentation.toLowerCase().equals("nan")) {
            return null;
        }
        return p.getDecimalValue();
    }
}
