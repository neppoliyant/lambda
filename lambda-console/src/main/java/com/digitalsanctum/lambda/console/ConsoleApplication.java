package com.digitalsanctum.lambda.console;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.AuthConfig;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;

import java.io.IOException;

public class ConsoleApplication {

    private static final String BUILDER_IMAGE = "digitalsanctum/lambda-builder";

    final String USER_HOME = System.getenv("HOME");

    // volumes
    final HostConfig hostConfig = HostConfig.builder().binds(
            USER_HOME + "/projects/lambda/import:/data/import",
            USER_HOME + "/projects/lambda/export:/data/export",
            USER_HOME + "/.m2/repository:/root/.m2/repository"
    ).build();

    private final DockerClient docker;
    private final String lambdaJar;
    private final String lambdaHandler;
    private final String lambdaResourcePath;

    public ConsoleApplication(DockerClient docker, String lambdaJar, String lambdaHandler, String lambdaResourcePath) {
        this.docker = docker;
        this.lambdaJar = lambdaJar;
        this.lambdaHandler = lambdaHandler;
        this.lambdaResourcePath = lambdaResourcePath;
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        String lambdaJar = args[0];
        String lambdaHandler = args[1];
        String lambdaResourcePath = args[2];

        // todo handle http method and timeout overrides

        DockerClient dockerClient = DefaultDockerClient.fromEnv().build();
        System.out.println("client = " + (System.currentTimeMillis() - start));
        String generateScript = "/data/generate.sh";
        new ConsoleApplication(dockerClient, lambdaJar, lambdaHandler, lambdaResourcePath)
//                .pull()
                .build(generateScript);

        System.out.println("time = " + (System.currentTimeMillis() - start));
    }

    private ConsoleApplication build(String generateScript) throws Exception {

        long startTime = System.currentTimeMillis();
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .image(BUILDER_IMAGE)
                .env(
                        "LAMBDA_JAR=" + this.lambdaJar,
                        "LAMBDA_HANDLER=" + this.lambdaHandler,
                        "LAMBDA_RESOURCE_PATH=" + this.lambdaResourcePath
                )
                .hostConfig(hostConfig)
                .cmd(generateScript)
                .build();

        final ContainerCreation creation = docker.createContainer(containerConfig);
        final String id = creation.id();

        docker.startContainer(id);
        System.out.println("start = " + (System.currentTimeMillis() - startTime));

        long execStart = System.currentTimeMillis();
        execInContainer(id, generateScript);
        System.out.println("generate = " + (System.currentTimeMillis() - execStart));

        try {
            docker.killContainer(id);
        } catch (Exception e) {
            e.printStackTrace();
            docker.killContainer(id);
        }

        docker.removeContainer(id);

        return this;
    }

    private void execInContainer(final String containerId, final String... command)
            throws DockerException, InterruptedException, IOException {
        final String execId = docker.execCreate(containerId, command,
                DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        final LogStream output = docker.execStart(execId);
//        output.attach(System.out, System.err);
        final String execOutput = output.readFully();
        System.out.println(execOutput);
    }

    private ConsoleApplication pull() throws DockerException, InterruptedException {
        AuthConfig authConfig = AuthConfig.builder()
                .email(System.getenv("DOCKER_EMAIL"))
                .username(System.getenv("DOCKER_USERNAME"))
                .password(System.getenv("DOCKER_PASSWORD"))
                .build();
        docker.pull(BUILDER_IMAGE, authConfig);
        return this;
    }
}