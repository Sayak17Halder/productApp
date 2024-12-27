package com.demo.product.productApp.validation;

import com.demo.product.productApp.repository.ProductRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueProductNameValidator implements ConstraintValidator<UniqueProductName, String> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public boolean isValid(String productName, ConstraintValidatorContext context) {
        if (productName == null || productName.isEmpty()) {
            return true; // Let other validations handle null or empty checks
        }

        return !productRepository.existsByProdName(productName);
    }
}
