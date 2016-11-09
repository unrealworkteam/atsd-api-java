package com.axibase.tsd.model.data.command;

import javax.ws.rs.core.Response.StatusType;

import com.axibase.tsd.model.system.ServerError;

public class BatchResponse {

    private final StatusType statusType;
    private ServerError serverError;
    private SendCommandResult sendCommandResult;

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

    public SendCommandResult getSendCommandResult() {
        return sendCommandResult;
    }

    public void setSendCommandResult(SendCommandResult sendCommandResult) {
        this.sendCommandResult = sendCommandResult;
    }

}
