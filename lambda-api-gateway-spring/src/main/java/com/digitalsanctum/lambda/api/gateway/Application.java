package com.digitalsanctum.lambda.api.gateway;

import com.digitalsanctum.lambda.Definition;
import com.digitalsanctum.lambda.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Value("${lambda.handler}")
    private String handler;

    @Value("${lambda.timeout}")
    private int timeout;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }

    @Bean
    public Executor executor() {
        return new Executor(new Definition(handler, timeout));
    }
}
