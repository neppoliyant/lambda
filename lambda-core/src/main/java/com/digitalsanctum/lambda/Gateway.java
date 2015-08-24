package com.digitalsanctum.lambda;

public class Gateway {

    private final String resourceName;
    private final String method;
    private final Definition definition;

    public Gateway(Definition definition, String resourceName, String method) {
        this.definition = definition;
        this.resourceName = resourceName;
        this.method = method;
    }

    public void generate() {

    }
}
