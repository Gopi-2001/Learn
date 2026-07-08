package com.project.razorpay.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ExpiryYearValidator  implements ConstraintValidator<ExpiryYear, Integer> {

    @Override
    public void initialize(ExpiryYear constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(Integer inputYear, ConstraintValidatorContext context) {
        // Null values are handled by @NotNull, not here
        if (inputYear == null) {
            return true;
        }

        int currentYear = Year.now().getValue();

        return inputYear >= currentYear;
    }
}