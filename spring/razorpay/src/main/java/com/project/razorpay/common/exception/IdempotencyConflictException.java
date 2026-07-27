package com.project.razorpay.common.exception;

import lombok.Getter;

@Getter
public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String message) {
        super(message);
    }

}
