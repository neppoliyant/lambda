package com.digitalsanctum.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
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

    @SuppressWarnings("unchecked")
    public ResultProvider execute(final Object input) throws Exception {

        if (this.definition.getHandler().contains("::")) {

            // TODO better algorithm around method selection

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

            Map<String, Class> handlerTypes = getRequestHandlerTypes(this.definition.getHandler());

            Class requestClass = handlerTypes.get("request");
            Object requestObj = input;
            if (input instanceof String && ((String) input).startsWith("{")) {
                ObjectMapper mapper = new ObjectMapper();
                requestObj = mapper.readValue(((String) input).getBytes(), requestClass);
            }

            invoke(requestObj, obj, cls.getDeclaredMethod("handleRequest", requestClass, Context.class));
        }

        return this;
    }

    public static Map<String, Class> getRequestHandlerTypes(String handler)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Map<String, Class> types = new HashMap<>();
        Class cls = Class.forName(handler);
        final Object obj = cls.newInstance();
        if (obj instanceof RequestHandler) {
            Type[] interfaces = cls.getGenericInterfaces();
            // TODO don't assume RequestHandler is the first/only interface
            ParameterizedType firstInterface = (ParameterizedType) interfaces[0];
            if (firstInterface.getActualTypeArguments().length == 2) {
                Class requestClass = (Class) firstInterface.getActualTypeArguments()[0];
                types.put("request", requestClass);

                Class responseClass = (Class) firstInterface.getActualTypeArguments()[1];
                types.put("response", responseClass);
            } else {
                throw new RuntimeException("unexpected number of type args " + firstInterface.getActualTypeArguments().length);
            }
        } else {
            throw new RuntimeException(obj.getClass() + " does not implement RequestHandler");
        }

        return types;
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
