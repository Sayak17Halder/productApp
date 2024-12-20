package com.demo.product.productApp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProductNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProductName {

    String message() default "Product name must be alphanumeric and can contain only '-' and '_'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
