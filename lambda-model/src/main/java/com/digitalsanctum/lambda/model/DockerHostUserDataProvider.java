package com.digitalsanctum.lambda.model;


public class DockerHostUserDataProvider implements UserDataProvider {

    private final DockerConfig dockerConfig;
    private final String imageName;
    private final String handler;
    private final int port;
    private final int timeout;

    public DockerHostUserDataProvider(DockerConfig dockerConfig, String imageName, String handler, int port, int timeout) {
        this.dockerConfig = dockerConfig;
        this.imageName = imageName;
        this.handler = handler;
        this.port = port;
        this.timeout = timeout;
    }

    public String getUserData() {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n")
                .append(String.format("docker login --email=%s --username=%s --password=%s",
                        dockerConfig.getEmail(), dockerConfig.getUsername(), dockerConfig.getPassword())).append("\n")
                .append(String.format("docker pull %s", imageName)).append("\n")
                .append(String.format("docker run -d -e \"LAMBDA_TIMEOUT=%s\" -e \"LAMBDA_HANDLER=%s\" -p %s:8080 %s",
                        timeout, handler, port, imageName));
        return sb.toString();
    }

}
