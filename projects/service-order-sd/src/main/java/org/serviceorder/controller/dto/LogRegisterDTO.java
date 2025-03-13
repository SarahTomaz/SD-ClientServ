package org.serviceorder.controller.dto;

public class LogRegisterDTO {

    private String operation, operationResponse, time;
    private int id;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperationResponse() {
        return operationResponse;
    }

    public void setOperationResponse(String operationResponse) {
        this.operationResponse = operationResponse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
