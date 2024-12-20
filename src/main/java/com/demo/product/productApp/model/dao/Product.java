package com.demo.product.productApp.model.dao;

import com.demo.product.productApp.validation.ValidProductName;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String id;
    @ValidProductName
    @NotBlank(message = "Product Name cannot be blank")
    private String prodName;

    @NotNull(message = "Product Price cannot be null")
    @Positive(message = "Product Price must be greater than zero")
    private Double prodPrice;

    @NotNull(message = "Date of Manufacture cannot be null")
    @PastOrPresent(message = "Date of Manufacture cannot be in the future")
    private LocalDate prodDateOfMan;

}

