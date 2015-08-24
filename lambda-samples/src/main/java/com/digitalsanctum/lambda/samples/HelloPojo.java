package com.digitalsanctum.lambda.samples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloPojo implements RequestHandler<TestRequest, TestResponse> {
    public TestResponse handleRequest(TestRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(request.toString());
        TestResponse response = new TestResponse();
        response.setMessage(request.getFirstName() + " " + request.getLastName());
        logger.log(response.toString());
        return response;
    }
}
