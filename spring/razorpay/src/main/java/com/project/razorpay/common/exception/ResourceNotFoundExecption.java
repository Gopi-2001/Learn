package com.project.razorpay.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundExecption extends RuntimeException {

    private final String resourceName;
    private final Object identifier;

    public ResourceNotFoundExecption(String  resourceName, Object identifier) {

        super(resourceName + " not found " + identifier);

        this.resourceName = resourceName;
        this.identifier = identifier;

    }
}
