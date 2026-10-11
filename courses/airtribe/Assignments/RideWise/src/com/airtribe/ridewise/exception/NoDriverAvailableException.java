package com.airtribe.ridewise.exception;

public class NoDriverAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoDriverAvailableException(String message) {
        super(message);
    }
}
