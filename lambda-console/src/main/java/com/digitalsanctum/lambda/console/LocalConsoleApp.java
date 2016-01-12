package com.digitalsanctum.lambda.console;

import com.digitalsanctum.lambda.generator.Generator;
import com.digitalsanctum.lambda.model.DockerConfig;
import com.digitalsanctum.lambda.model.LambdaConfig;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalConsoleApp {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        String lambdaJar = args[0];
        String lambdaHandler = args[1];
        String lambdaResourcePath = args[2];
        String httpMethod = args[3];

        String imageName = "digitalsanctum/lambda-api";
        Path lambdaSrcDir = Paths.get(System.getenv("HOME"), "projects", "lambda");
        Path apiDockerfilePath = lambdaSrcDir.resolve("export");
        Path apiJarPath = apiDockerfilePath.resolve("api.jar");

        DockerConfig dockerConfig = new DockerConfig.Builder().fromEnv().build();

        LambdaConfig lambdaConfig = new LambdaConfig.Builder(lambdaJar, lambdaHandler, imageName, dockerConfig)
                .lambdaSrcDir(lambdaSrcDir)
                .apiJarPath(apiJarPath.toString())
                .resourcePath(lambdaResourcePath)
                .httpMethod(httpMethod)
                .build();


        // build API gateway
        long buildStart = System.currentTimeMillis();

        // dynamically adds lambda jar to classloader
        addLambdaJar(new File(lambdaConfig.getLambdaJarPath()));

        new Generator(lambdaConfig)
                .installLambdaJar()
                .generateJerseyResource()
                .compileAndPackageGateway()
                .exportGatewayJar();
        System.out.println("build gateway time=" + (System.currentTimeMillis() - buildStart));


        // build and push API gateway docker image
        long pushStart = System.currentTimeMillis();
        new DockerImageBuilder(apiDockerfilePath, imageName)
                .build();
//                .push();
        System.out.println("build/push docker image time=" + (System.currentTimeMillis() - pushStart));


        // provision
        /*int port = 80;
        int timeout = 3;
        DigitalOceanConfig doConfig = new DigitalOceanConfig.Builder().fromEnv()
                .userDataProvider(new DockerHostUserDataProvider(dockerConfig, imageName, lambdaHandler, port, timeout))
                .hostname("test")
                .size("512mb")
                .region("tor1")
                .build();

        long provStart = System.currentTimeMillis();
        Provisioner provisioner = new Provisioner(doConfig);
        Integer dropletId = provisioner.createDroplet();
        provisioner.waitForDropletCreation(dropletId);
        System.out.println("provision time = " + (System.currentTimeMillis() - provStart));*/

        System.out.println("time = " + (System.currentTimeMillis() - start));
    }

    private static void addLambdaJar(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }
}
