package com.digitalsanctum.lambda.logging;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SystemOutLambaLogger implements LambdaLogger {
    public void log(String s) {
        System.out.println(s);
    }
}
