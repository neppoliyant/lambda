package com.digitalsanctum.lambda.context;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.digitalsanctum.lambda.logging.SystemOutLambaLogger;

public class SimpleContext implements Context {

    private final String functionName;
    private final String awsRequestId;

    public SimpleContext(String awsRequestId, String functionName) {
        this.awsRequestId = awsRequestId;
        this.functionName = functionName;
    }

    public String getAwsRequestId() {
        return this.awsRequestId;
    }

    public String getLogGroupName() {
        return null;
    }

    public String getLogStreamName() {
        return null;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public CognitoIdentity getIdentity() {
        return null;
    }

    public ClientContext getClientContext() {
        return null;
    }

    public int getRemainingTimeInMillis() {
        return 0;
    }

    public int getMemoryLimitInMB() {
        return 0;
    }

    public LambdaLogger getLogger() {
        return new SystemOutLambaLogger();
    }
}
