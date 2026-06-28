package com.project.razorpay.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleException(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getErrorCode(),ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundExecption.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundExecption ex) {
        String errorCode = ex.getResourceName().toUpperCase() + "_NOT_FOUND";

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(errorCode,ex.getMessage()));
    }
}
