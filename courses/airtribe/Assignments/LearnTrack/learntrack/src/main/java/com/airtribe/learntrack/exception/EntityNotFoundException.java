package com.airtribe.learntrack.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String messageString) {
        super("Non-existent " + messageString);
    }
    
}
