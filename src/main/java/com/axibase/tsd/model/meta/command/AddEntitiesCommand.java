package com.axibase.tsd.model.meta.command;

import com.axibase.tsd.model.meta.Entity;
import com.axibase.tsd.util.AtsdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddEntitiesCommand extends AbstractEntitiesCommand {
    private Boolean createEntities;

    public AddEntitiesCommand() {
        super(AtsdUtil.ADD_COMMAND);
    }

    public AddEntitiesCommand(Boolean createEntities, List<Entity> entities) {
        this();
        this.createEntities = createEntities;
        this.entities = entities;
    }

    public Boolean getCreateEntities() {
        return createEntities;
    }

}
