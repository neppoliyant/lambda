package com.digitalsanctum.lambda.samples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time implements RequestHandler<String, String> {

    public String handleRequest(String format, Context context) {
        LambdaLogger logger = context.getLogger();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = sdf.format(new Date());
        logger.log(result);
        return result;
    }
}
