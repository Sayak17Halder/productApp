package com.demo.product.productApp.repository;

import com.demo.product.productApp.model.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String>, CustomProductRepository {

    @Query("{'prodName': { $regex: ?0, $options: 'i' }}")
    List<ProductEntity> findByNameRegex(String name);

    List<ProductEntity> findByProdPriceBetween(Double minPrice, Double maxPrice);

    boolean existsByProdName(String prodName);
}

