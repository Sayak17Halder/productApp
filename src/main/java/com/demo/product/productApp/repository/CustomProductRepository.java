package com.demo.product.productApp.repository;

import com.demo.product.productApp.model.entity.ProductEntity;

import java.util.List;

public interface CustomProductRepository {
    List<ProductEntity> findProductsByPriceRange(Double minPrice, Double maxPrice);
}

