package com.digitalsanctum.lambda.socket.activation;

import com.digitalsanctum.lambda.socket.activation.model.SocketActivationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Closeables;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author shane
 */
public class ConfigurationFactory {

    public SocketActivationConfiguration getConfiguration(InputStream in) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SocketActivationConfiguration sac = objectMapper.readValue(in, SocketActivationConfiguration.class);
        Closeables.closeQuietly(in);
        return sac;
    }
}
