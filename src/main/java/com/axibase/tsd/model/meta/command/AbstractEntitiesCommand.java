package com.axibase.tsd.model.meta.command;

import com.axibase.tsd.model.meta.Entity;
import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
public class AbstractEntitiesCommand extends SimpleCommand {
    protected List<Entity> entities;

    public AbstractEntitiesCommand(String action) {
        super(action);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
