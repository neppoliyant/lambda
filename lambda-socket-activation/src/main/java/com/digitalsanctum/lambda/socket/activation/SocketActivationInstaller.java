package com.digitalsanctum.lambda.socket.activation;

import com.digitalsanctum.lambda.socket.activation.model.SocketActivationConfiguration;
import com.google.common.base.Joiner;
import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shane
 */
public class SocketActivationInstaller {

    private static final Logger log = LoggerFactory.getLogger(SocketActivationInstaller.class);

    private final String systemdRootPath = "/etc/systemd/system";
    private DockerClient dockerClient;
    private SudoExecutor sudoExecutor;
    private final String configurationFilePath;

    public SocketActivationInstaller(SudoExecutor sudoExecutor, String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;
        this.sudoExecutor = sudoExecutor;
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

    public static void main(String[] args) throws Exception {

        String sudoerPass = args[0];
        String configurationFilePath = "/testSocketConf.json";

        SudoExecutor sudoExecutor = new SudoExecutor(sudoerPass);
        new SocketActivationInstaller(sudoExecutor, configurationFilePath)
                .install();
    }

    public void install() throws DockerException, InterruptedException, IOException {
        InputStream in = SocketActivationInstaller.class.getResourceAsStream(configurationFilePath);
        SocketActivationConfiguration sac = new ConfigurationFactory().getConfiguration(in);

        createContainer(sac);

        sudoExecutor.systemCtl("stop test-docker");
        sudoExecutor.systemCtl("stop test-proxy");

        // cp test-proxy.socket
        String socketDescriptorFilePath = sac.getSocketDescriptorFilePath();
        writeToSystemd(socketDescriptorFilePath, sac.getSocketDescriptorFileContent());

        // cp test-proxy.service
        writeToSystemd(sac.getProxyServiceFilePath(), sac.getProxyServiceFileContent());

        // cp test-docker.service
        final String waitPortDir = "/usr/local/bin";
        installWaitPortScript(sac, waitPortDir);
        writeToSystemd(sac.getDockerServiceFilePath(), sac.getDockerServiceFileContent(waitPortDir));

        sudoExecutor.systemCtl("enable " + socketDescriptorFilePath);
        sudoExecutor.systemCtl("daemon-reload");
        sudoExecutor.systemCtl("start " + sac.getProxyServiceFilePath());

        log.info("done");
    }

    private void createContainer(SocketActivationConfiguration sac) throws DockerException, InterruptedException {
        final String containerName = sac.getName();
        try {
            dockerClient.stopContainer(containerName, 5);
            dockerClient.removeContainer(containerName);
        } catch (ContainerNotFoundException cnfe) {
            // no op
        }

        // step 1: create docker container
        log.info("creating docker container");
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        List<PortBinding> hostPorts = Collections.singletonList(PortBinding.of("0.0.0.0", sac.getProxyServicePort()));
        portBindings.put(String.valueOf(sac.getSocketListenStreamPort()), hostPorts);

        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();
        try {
            final ContainerCreation containerCreation = dockerClient.createContainer(
                    ContainerConfig.builder()
                            .exposedPorts(String.valueOf(sac.getSocketListenStreamPort()))
                            .hostConfig(hostConfig)
                            .image(sac.getDockerImage())
                            .build(),
                    containerName
            );
            log.info("created container {}", containerCreation.toString());

        } catch (DockerRequestException dre) {
            if (409 == dre.status()) {
                log.warn("409 conflict; container already exists");
            } else {
                throw dre;
            }
        } finally {
            dockerClient.close();
        }
    }

    private void installWaitPortScript(SocketActivationConfiguration sac, String waitPortDir) throws IOException {
        log.info("creating waitport script");
        Path waitPortPath = Paths.get(waitPortDir, sac.getWaitPortPath());
        if (Files.exists(waitPortPath)) {
            log.info("{} already exists", waitPortPath);
        } else {
            log.info("creating {}", waitPortPath);
            writeToDir(sac.getWaitPortPath(), sac.getWaitportFileContent(), waitPortDir);
            sudoExecutor.makeExecutable(waitPortPath);
        }
    }

    private Path writeToSystemd(String path, String content) throws IOException {
        return writeToDir(path, content, systemdRootPath);
    }

    private Path writeToDir(String path, String content, String dir) throws IOException {
        Path tmpFile = writeToFile(Paths.get(path), content);
        Path dest = Paths.get(dir, path);
        String[] cmd = {"cp", tmpFile.toString(), dest.toString()};
        log.info("copying {} to {}", path, dest);
        sudoExecutor.executeSystemCmdAsSudoer(Joiner.on(' ').join(cmd));
        return dest;
    }

    private Path writeToFile(Path path, String content) throws IOException {
        return Files.write(path, content.getBytes("utf-8"));
    }
}
