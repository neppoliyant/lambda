package com.digitalsanctum.lambda.samples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloWorld implements RequestHandler<String, String> {

    public String handleRequest(String input, Context context) {
        LambdaLogger logger = context.getLogger();
        String result = "Hello " + input;
        logger.log(result);
        return result;
    }
}
