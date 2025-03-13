package org.serviceorder.database.entities;

import org.serviceorder.controller.dto.LogRegisterDTO;

public class LogRegister {

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

    public static LogRegister convert(LogRegisterDTO dto) {
        LogRegister log_register = new LogRegister();
        log_register.setOperation(dto.getOperation());
        log_register.setTime(dto.getTime());
        log_register.setOperationResponse(dto.getOperationResponse());
        return log_register;
    }
}
