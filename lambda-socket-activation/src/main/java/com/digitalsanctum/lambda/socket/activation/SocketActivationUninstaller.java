package com.digitalsanctum.lambda.socket.activation;

import com.digitalsanctum.lambda.socket.activation.model.SocketActivationConfiguration;
import com.google.common.base.Joiner;
import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.AuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author shane
 */
public class SocketActivationUninstaller {

    private static final Logger log = LoggerFactory.getLogger(SocketActivationInstaller.class);

    private final String systemdRootPath = "/etc/systemd/system";
    private DockerClient dockerClient;
    private SudoExecutor sudoExecutor;
    private final String configurationFilePath;

    public SocketActivationUninstaller(SudoExecutor sudoExecutor, String configurationFilePath) {
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

        new SocketActivationUninstaller(new SudoExecutor(sudoerPass), configurationFilePath)
                .uninstall();
    }

    public void uninstall() throws IOException, DockerException, InterruptedException {
        InputStream in = SocketActivationInstaller.class.getResourceAsStream(configurationFilePath);
        SocketActivationConfiguration sac = new ConfigurationFactory().getConfiguration(in);

        removeContainer(sac);

        sudoExecutor.systemCtl("stop test-docker");
        sudoExecutor.systemCtl("stop test-proxy");
        sudoExecutor.systemCtl("disable test-proxy");
        sudoExecutor.systemCtl("daemon-reload");

        // delete test-proxy.socket
        delete(sac.getSocketDescriptorFilePath());

        // delete test-proxy.service
        delete(sac.getProxyServiceFilePath());

        // delete test-docker.service
        delete(sac.getDockerServiceFilePath());

        log.info("done");
    }

    private void removeContainer(SocketActivationConfiguration sac) throws DockerException, InterruptedException {
        final String containerName = sac.getName();
        try {
            dockerClient.stopContainer(containerName, 5);
            dockerClient.removeContainer(containerName);
        } catch (ContainerNotFoundException cnfe) {
            log.warn(cnfe.getMessage());
        } finally {
            dockerClient.close();
        }
    }

    private Path delete(String path) throws IOException {
        Path dest = Paths.get(systemdRootPath, path);
        String[] cmd = {"rm", dest.toString()};
        log.info("removing {}", dest);
        sudoExecutor.executeSystemCmdAsSudoer(Joiner.on(' ').join(cmd));
        return dest;
    }
}
