package com.digitalsanctum.lambda.model;

import static java.lang.System.getenv;

public class DockerConfig {

    private final String email;
    private final String username;
    private final String password;
    private String serverAddress;

    private DockerConfig(Builder builder) {
        this.email = builder.email;
        this.username = builder.username;
        this.password = builder.password;
        this.serverAddress = builder.serverAddress;
    }

    public static class Builder {
        private String email;
        private String username;
        private String password;
        private String serverAddress;

        public Builder fromEnv() {
            this.email = getenv("DOCKER_EMAIL");
            this.username = getenv("DOCKER_USERNAME");
            this.password = getenv("DOCKER_PASSWORD");
            return this;
        }

        public Builder serverAddress(String serverAddress) {
            this.serverAddress = serverAddress;
            return this;
        }

        public DockerConfig build() {
            return new DockerConfig(this);
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getUsername() {
        return username;
    }
}
