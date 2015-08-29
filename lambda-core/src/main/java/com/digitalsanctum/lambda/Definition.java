package com.digitalsanctum.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.digitalsanctum.lambda.context.SimpleContext;

import java.util.UUID;

public class Definition {
    private final String handler;
    private String name;
    private int timeout = 60;

    public Definition(String handler) {
        this.handler = handler;
    }

    public Definition(String handler, int timeout) {
        this.handler = handler;
        this.timeout = timeout;
    }

    public String getHandler() {
        return handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuntime() {
        return "java8";
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Context getContext() {
        String requestId = UUID.randomUUID().toString();
        return new SimpleContext(requestId, this.name);
    }

    public String getHandlerClass() {
        return this.handler.split("::")[0];
    }

    public String getHandlerMethod() {
        return this.handler.split("::")[1];
    }
}
