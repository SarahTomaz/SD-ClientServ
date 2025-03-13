package org.serviceorder.server;

import com.google.gson.Gson;

public class RequestModel {
    public String method;
    public String router;
    public String operation;

    public RequestModel(String method, String router, String operation) {
        this.method = method;
        this.router = router;
        this.operation = operation;
    }
}
