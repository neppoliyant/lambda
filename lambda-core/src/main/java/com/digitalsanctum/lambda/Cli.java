package com.digitalsanctum.lambda;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Cli {

    public static void main(String[] args) throws Exception {


        String path = args[0]; // path to shaded jar including aws-lambda-java-core
        String handler = args[1];
        String input = args[2];

        // todo validation

        Definition def = new Definition(handler, 3);

        File lambdaJar = new File(path);
        if (!lambdaJar.exists()) {
            throw new RuntimeException("Lambda jar not found: " + lambdaJar.getAbsolutePath());
        }
        // temporary hack until we get going with assembling containers
        addLambdaJar(lambdaJar);

        Executor executor = new Executor(def);
        executor.execute(input).getResult();
    }

    private static void addLambdaJar(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }
}
