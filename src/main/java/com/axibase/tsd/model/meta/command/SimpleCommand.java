package com.axibase.tsd.model.meta.command;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleCommand {
    protected final String action;

    public SimpleCommand(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
