package com.axibase.tsd.model.data.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Igor Shmagrinskiy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendCommandResult {

    private Integer fail;
    private Integer success;
    private Integer total;
    private Integer stored;
    private String error;

    public Integer getFail() {
        return fail;
    }

    public void setFail(Integer fail) {
        this.fail = fail;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getStored() {
        return stored;
    }

    public void setStored(Integer stored) {
        this.stored = stored;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return String.format("{\"fail\":%s,\"success\":%s,\"total\":%s,\"stored\":%s,\"error\":%s}",
                fail, success, total, stored, error
        );
    }
}
