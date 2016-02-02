package com.digitalsanctum.lambda.socket.activation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author shane
 */
public class SudoExecutor {

    private static final Logger log = LoggerFactory.getLogger(SudoExecutor.class);

    private String sudoerPassword;

    public SudoExecutor(String sudoerPassword) {
        this.sudoerPassword = sudoerPassword;
    }

    public void systemCtl(String args) throws IOException {
        executeSystemCmdAsSudoer("systemctl " + args);
    }

    public void executeSystemCmdAsSudoer(String cmd) throws IOException {
        String[] resolvedCmd = {"/bin/bash", "-c", "echo \"" + sudoerPassword + "\"| sudo -S " + cmd};
        log.info(Arrays.toString(resolvedCmd).replace(sudoerPassword, "******"));
        ProcessBuilder pb = new ProcessBuilder(resolvedCmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        logProcessOutput(p);
    }

    public void logProcessOutput(Process p) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ( (line = reader.readLine()) != null) {
            builder.append(">>").append(line).append(System.getProperty("line.separator"));
        }
        String result = builder.toString();
        log.info(result);
    }

    public void makeExecutable(Path path) throws IOException {
        executeSystemCmdAsSudoer("chmod +x " + path.toString());
    }
}
