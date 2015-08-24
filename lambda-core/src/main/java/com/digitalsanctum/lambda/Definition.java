package com.digitalsanctum.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.digitalsanctum.lambda.context.SimpleContext;

import java.util.UUID;

public class Definition {
    private final String name;
    private final String handler;
    private int timeout = 60;

    public Definition(String handler, String name) {
        this.handler = handler;
        this.name = name;
    }

    public String getHandler() {
        return handler;
    }

    public String getName() {
        return name;
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
