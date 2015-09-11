package com.digitalsanctum.lambda.api.gateway;

import com.digitalsanctum.lambda.Definition;
import com.digitalsanctum.lambda.Executor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class App {
    public static void main(String[] args) throws Exception {

        String lambdaHandler = System.getenv("LAMBDA_HANDLER");
        int lambdaTimeout = Integer.parseInt(System.getenv("LAMBDA_TIMEOUT"));

        System.out.println("LAMBDA_HANDLER=" + lambdaHandler);
        System.out.println("LAMBDA_TIMEOUT=" + lambdaTimeout);

        Executor executor = new Executor(new Definition(lambdaHandler, lambdaTimeout));

        EntryPoint entryPoint = new EntryPoint(executor);
        ResourceConfig rc = new ResourceConfig();
        rc.register(entryPoint);

        ServletContainer sc = new ServletContainer(rc);
        ServletHolder holder = new ServletHolder(sc);

        ServletContextHandler sch = new ServletContextHandler();
        sch.setContextPath("/");
        sch.addServlet(holder, "/*");

        Server server = new Server(8080);
        server.setHandler(sch);
        server.start();
        server.join();
    }

}
