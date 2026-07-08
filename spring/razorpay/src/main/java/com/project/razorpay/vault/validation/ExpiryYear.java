package com.project.razorpay.vault.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExpiryYearValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiryYear {
    String message() default "Expiry year must be the current year or a future year";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}