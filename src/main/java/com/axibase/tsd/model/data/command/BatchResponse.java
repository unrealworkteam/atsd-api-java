package com.axibase.tsd.model.data.command;

import javax.ws.rs.core.Response.StatusType;

import com.axibase.tsd.model.system.ServerError;

public class BatchResponse {

    private final StatusType statusType;
    private ServerError serverError;
    private SendCommandResult result;

    public BatchResponse(StatusType statusType) {
        this.statusType = statusType;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public ServerError getServerError() {
        return serverError;
    }

    public void setServerError(ServerError serverError) {
        this.serverError = serverError;
    }

    public SendCommandResult getResult() {
        return result;
    }

    public void setResult(SendCommandResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "BatchResponse {statusType=" + statusType + ", serverError=" + serverError + ", result=" + result + "}";
    }

}
