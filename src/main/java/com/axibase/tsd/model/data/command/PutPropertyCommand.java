package com.axibase.tsd.model.data.command;

import com.axibase.tsd.model.data.PropertyKey;
import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutPropertyCommand {
    private PropertyKey key;
    @JsonProperty("values")
    private Map<String,String> values;
    private long timestamp;

    public PutPropertyCommand() {
    }

    public PropertyKey getKey() {
        return key;
    }

    public void setKey(PropertyKey key) {
        this.key = key;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    @JsonIgnore
    public void setValues(String... namesAndValues) {
        this.values = AtsdUtil.toMap(namesAndValues);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PropertyPutCommand{" +
                "key=" + key +
                ", values=" + values +
                ", timestamp=" + timestamp +
                '}';
    }
}
