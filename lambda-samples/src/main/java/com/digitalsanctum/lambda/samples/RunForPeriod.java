package com.digitalsanctum.lambda.samples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RunForPeriod implements RequestHandler<String, Void> {

    public Void handleRequest(String in, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("runForPeriodInMillis: " + in);
        try {
            Thread.sleep(Long.valueOf(in));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
