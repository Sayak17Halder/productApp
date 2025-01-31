package com.demo.product.productApp.service;


import com.demo.product.productApp.model.entity.ProductEntity;
import com.demo.product.productApp.model.dao.Product;
import com.demo.product.productApp.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Add Product
    @CacheEvict(value = "products", allEntries = true)
    public Product addProduct(Product product) {
        ProductEntity productEntity = toEntity(product);
        ProductEntity savedEntity = productRepository.save(productEntity);
        return toDTO(savedEntity);
    }

    // Get All Products with Pagination and Sorting
    @Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size + ':sortBy:' + #sortBy + ':order:' + #order")
    public Page<Product> getAllProducts(int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return productRepository
                .findAll(PageRequest.of(page, size, sort))
                .map(this::toDTO);
    }

    // Get Product by ID
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(String id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with ID " + id + " not found."));
        return toDTO(entity);
    }

    // Delete Product
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),          // Evict single product by ID
            @CacheEvict(value = "products", allEntries = true)   // Evict all products (pagination cache)
    })
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product with ID " + id + " not found.");
        }
        productRepository.deleteById(id);
    }

    // Update Product
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(String id, Product product) {
        ProductEntity existingEntity = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with ID " + id + " not found."));
        existingEntity.setProdName(product.getProdName());
        existingEntity.setProdPrice(product.getProdPrice());
        existingEntity.setProdDateOfMan(product.getProdDateOfMan());
        ProductEntity updatedEntity = productRepository.save(existingEntity);
        return toDTO(updatedEntity);
    }

    // Search Products by Name
    @Cacheable(value = "productsByName", key = "#name")
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameRegex(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Find Products by Price Range
    public List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByProdPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get Products by Price Range using Mongo Template
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<ProductEntity> entities = productRepository.findProductsByPriceRange(minPrice, maxPrice);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Entity to dto
    public Product toDTO(ProductEntity productEntity){
        Product product = new Product();
        BeanUtils.copyProperties(productEntity, product);
        return product;
    }

    // dto to Entity
    public ProductEntity toEntity(Product product){
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(product, productEntity);
        return productEntity;
    }

}
