package com.example.FirstSpring;

public class HelloResponse {
    private String message;

    public HelloResponse(String string) {
        this.message = string;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
