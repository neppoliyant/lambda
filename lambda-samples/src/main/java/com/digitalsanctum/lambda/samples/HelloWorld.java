package com.digitalsanctum.lambda.samples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class HelloWorld {

    public String hello(String input, Context context) {
        LambdaLogger logger = context.getLogger();
        String result = "Hello " + input;
        logger.log(result);
        return result;
    }

    public void runForPeriodInMillis(String in, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("runForPeriodInMillis: " + in);
        try {
            Thread.sleep(Long.valueOf(in));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
