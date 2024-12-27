package com.demo.product.productApp.repository;

import com.demo.product.productApp.model.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CustomProductRepositoryImpl implements CustomProductRepository{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<ProductEntity> findProductsByPriceRange(Double minPrice, Double maxPrice) {
        Query query = new Query();
        query.addCriteria(Criteria.where("prodPrice").gte(minPrice).lte(maxPrice));
        return mongoTemplate.find(query, ProductEntity.class);
    }
}
