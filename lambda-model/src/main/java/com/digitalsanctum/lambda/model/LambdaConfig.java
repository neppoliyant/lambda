package com.digitalsanctum.lambda.model;

import java.nio.file.Path;

public class LambdaConfig {

    private String lambdaJarPath;
    private String handler;
    private String resourcePath;
    private String httpMethod;
    private int timeout;
    private int maxMemory;
    private String apiJarPath;
    private int port;
    private String imageName;

    private DockerConfig dockerConfig;

    private Path lambdaSrcDir;

    private LambdaConfig(Builder builder) {
        this.lambdaJarPath = builder.lambdaJarPath;
        this.handler = builder.handler;
        this.resourcePath = builder.resourcePath;
        this.httpMethod = builder.httpMethod;
        this.timeout = builder.timeout;
        this.maxMemory = builder.maxMemory;
        this.apiJarPath = builder.apiJarPath;
        this.port = builder.port;
        this.imageName = builder.imageName;
        this.dockerConfig = builder.dockerConfig;
        this.lambdaSrcDir = builder.lambdaSrcDir;
    }

    public static class Builder {
        private String lambdaJarPath;
        private String handler;
        private String resourcePath = "/";
        private String httpMethod = "POST";
        private int timeout = 3;
        private int maxMemory = 256;
        private String apiJarPath;
        private int port = 80;
        private String imageName;

        private DockerConfig dockerConfig;

        private Path lambdaSrcDir;

        public Builder(String lambdaJarPath, String handler, String imageName, DockerConfig dockerConfig) {
            this.lambdaJarPath = lambdaJarPath;
            this.handler = handler;
            this.imageName = imageName;
            this.dockerConfig = dockerConfig;
        }

        public Builder lambdaSrcDir(Path lambdaSrcDir) {
            this.lambdaSrcDir = lambdaSrcDir;
            return this;
        }

        public Builder resourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder maxMemory(int maxMemory) {
            this.maxMemory = maxMemory;
            return this;
        }

        public Builder apiJarPath(String apiJarPath) {
            this.apiJarPath = apiJarPath;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public LambdaConfig build() {
            return new LambdaConfig(this);
        }
    }

    public String getLambdaJarPath() {
        return lambdaJarPath;
    }

    public String getHandler() {
        return handler;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public String getApiJarPath() {
        return apiJarPath;
    }

    public int getPort() {
        return port;
    }

    public String getImageName() {
        return imageName;
    }

    public DockerConfig getDockerConfig() {
        return dockerConfig;
    }

    public Path getLambdaSrcDir() {
        return lambdaSrcDir;
    }

    public Path getApiGatewayModuleRoot() {
        return lambdaSrcDir.resolve("lambda-api-gateway-jersey");
    }
}
