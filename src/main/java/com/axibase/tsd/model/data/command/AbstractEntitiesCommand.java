package com.axibase.tsd.model.data.command;

import com.axibase.tsd.model.meta.Entity;
import com.axibase.tsd.util.AtsdUtil;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
public class AbstractEntitiesCommand {
    protected List<Entity> entities;
    private final String action;

    public AbstractEntitiesCommand(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
