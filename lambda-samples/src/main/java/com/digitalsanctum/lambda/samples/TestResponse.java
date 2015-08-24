package com.digitalsanctum.lambda.samples;

public class TestResponse {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "TestResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
