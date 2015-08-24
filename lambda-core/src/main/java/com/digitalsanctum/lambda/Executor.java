package com.digitalsanctum.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Executor implements ResultProvider {

    private final Definition definition;
    private Object result;

    public Executor(Definition definition) {
        this.definition = definition;
    }

    public Definition getDefinition() {
        return definition;
    }

    @SuppressWarnings("unchecked")
    public ResultProvider execute(final Object input) throws Exception {

        // todo better algorithm around method selection


        if (this.definition.getHandler().contains("::")) {
            Class cls = Class.forName(this.definition.getHandlerClass());
            Method[] declaredMethods = cls.getDeclaredMethods();
            Method selectedMethod = null;
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getName().equals(this.definition.getHandlerMethod())) {
                    selectedMethod = declaredMethod;
                    break;
                }
            }
            if (selectedMethod == null) {
                throw new RuntimeException(String.format("Handler method '%s' not found in class '%s'",
                        this.definition.getHandlerMethod(), this.definition.getHandlerClass()));
            }

            final Object obj = cls.newInstance();
            final Method method = cls.getDeclaredMethod(this.definition.getHandlerMethod(), selectedMethod.getParameterTypes());

            invoke(input, obj, method);

        } else {
            Class cls = Class.forName(this.definition.getHandler());
            final Object obj = cls.newInstance();
            if (obj instanceof RequestHandler) {
                Type[] interfaces = cls.getGenericInterfaces();
                ParameterizedType firstInterface = (ParameterizedType) interfaces[0];
                if (firstInterface.getActualTypeArguments().length == 2) {
                    Class requestClass = (Class) firstInterface.getActualTypeArguments()[0];
                    Class responseClass = (Class) firstInterface.getActualTypeArguments()[1];

                    Object requestObj = input;
                    if (input instanceof String && ((String) input).startsWith("{")) {
                        ObjectMapper mapper = new ObjectMapper();
                        requestObj = mapper.readValue(((String) input).getBytes(), requestClass);
                    }

                    invoke(requestObj, obj, cls.getDeclaredMethod("handleRequest", requestClass, Context.class));

                } else {
                    throw new RuntimeException("unexpected number of type args " + firstInterface.getActualTypeArguments().length);
                }
            } else {
                throw new RuntimeException(obj.getClass() + " does not implement RequestHandler");
            }
        }

        return this;
    }

    public Object getResult() {
        return this.result;
    }

    private void invoke(Object input, Object obj, Method method) {
        final Context context = this.definition.getContext();
        LambdaLogger logger = context.getLogger();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = (Future) executor.submit(() -> {
            try {
                result = method.invoke(obj, input, context);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.log("Error invoking " + this.definition.getHandler());
            }
        });

        int timeout = this.definition.getTimeout();

        logger.log("START request: " + context.getAwsRequestId());
        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.log("TIMED OUT");
        } catch (Exception e) {
            future.cancel(true);
        }

        executor.shutdownNow();

        logger.log("END request: " + context.getAwsRequestId());
    }

}
