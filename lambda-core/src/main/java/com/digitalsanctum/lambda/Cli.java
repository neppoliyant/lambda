package com.digitalsanctum.lambda;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Cli {

    public static void main(String[] args) throws Exception {

        String name = args[0];
        String path = args[1]; // path to shaded jar including aws-lambda-java-core
        String handler = args[2];
        String input = args[3];

        // todo validation

        Definition def = new Definition(handler, name);
        def.setTimeout(3);

        File lambdaJar = new File(path);
        if (!lambdaJar.exists()) {
            throw new RuntimeException("Lambda jar not found: " + lambdaJar.getAbsolutePath());
        }
        // temporary hack until we get going with assembling containers
        addLambdaJar(lambdaJar);

        Executor executor = new Executor(def);
        Object result = executor.execute(input).getResult();

        System.out.println(result);
    }

    private static void addLambdaJar(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }
}
