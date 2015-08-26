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

    @Value("${lambda.name}")
    private String name;

    @Value("${lambda.handler}")
    private String handler;

    @Value("${lambda.timeout}")
    private int timeout;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }

    @Bean
    public Definition definition() {
        Definition def = new Definition(handler, name);
        def.setTimeout(timeout);
        return def;
    }

    @Bean
    public Executor executor() {
        return new Executor(definition());
    }
}
