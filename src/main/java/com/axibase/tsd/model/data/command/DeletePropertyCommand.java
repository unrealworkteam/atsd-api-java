package com.axibase.tsd.model.data.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeletePropertyCommand {
    private long timestamp;
    private List<String> keys;

    public DeletePropertyCommand() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "PropertyDeleteCommand{" +
                "timestamp=" + timestamp +
                ", keys=" + keys +
                '}';
    }
}
