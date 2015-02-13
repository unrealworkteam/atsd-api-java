package com.axibase.tsd.model.data.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Nikolay Malevanny.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsertPropertiesCommand {
    private List<PutPropertyCommand> puts;
    private DeletePropertyCommand delete;

    public InsertPropertiesCommand() {
    }

    public List<PutPropertyCommand> getPuts() {
        return puts;
    }

    public void setPuts(List<PutPropertyCommand> puts) {
        this.puts = puts;
    }

    public DeletePropertyCommand getDelete() {
        return delete;
    }

    public void setDelete(DeletePropertyCommand delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "PropertyBatchUpdateCommand{" +
                "puts=" + puts +
                ", delete=" + delete +
                '}';
    }
}
