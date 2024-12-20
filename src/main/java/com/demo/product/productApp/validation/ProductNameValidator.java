package com.demo.product.productApp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ValidProductName, String> {

    private static final String PRODUCT_NAME_PATTERN = "^[a-zA-Z0-9-_ ]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false; // Product name cannot be null or empty
        }
        return value.matches(PRODUCT_NAME_PATTERN); // Validate against regex
    }
}
