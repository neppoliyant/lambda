package com.digitalsanctum.lambda.console;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.AuthConfig;

import java.io.IOException;
import java.nio.file.Path;

public class DockerImageBuilder {

    private final Path apiDockerfilePath;
    private final String dockerImageName;

    private DockerClient dockerClient;

    public DockerImageBuilder(Path apiDockerfilePath, String dockerImageName) {
        this.apiDockerfilePath = apiDockerfilePath;
        this.dockerImageName = dockerImageName;
        try {
            AuthConfig authConfig = AuthConfig.builder()
                    .email(System.getenv("DOCKER_EMAIL"))
                    .username(System.getenv("DOCKER_USERNAME"))
                    .password(System.getenv("DOCKER_PASSWORD"))
                    .serverAddress("https://index.docker.io/v1/")
                    .build();
            dockerClient = DefaultDockerClient.fromEnv()
                    .authConfig(authConfig).build();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public DockerImageBuilder build() throws DockerCertificateException, InterruptedException, DockerException, IOException {
        dockerClient.build(this.apiDockerfilePath, this.dockerImageName);
        return this;
    }

    public void push() throws DockerException, InterruptedException {
        dockerClient.push(this.dockerImageName);
    }
}
