package com.demo.product.productApp.service;

import com.demo.product.productApp.entity.ProductEntity;
import com.demo.product.productApp.model.Product;
import com.demo.product.productApp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_shouldReturnSavedProduct() {
        Product product = new Product("1", "Test Product", 100.0, LocalDate.now());
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(product, productEntity);

        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        Product result = productService.addProduct(product);

        assertNotNull(result.getId());
        assertEquals("Test Product", result.getProdName());
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void getAllProducts_shouldReturnPaginatedProducts() {
        List<ProductEntity> entities = Arrays.asList(
                new ProductEntity("100","Product 1", 50.0, LocalDate.now(), null, null),
                new ProductEntity("101","Product 2", 60.0, LocalDate.now(), null, null)
        );
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("prodName").ascending());
        when(productRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(entities));

        Page<Product> result = productService.getAllProducts(0, 10, "prodName", "asc");

        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void getProductById_shouldReturnProduct_whenIdExists() {
        Product product = new Product("100", "Test Product", 100.0, LocalDate.now());
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(product, productEntity);

        when(productRepository.findById("100")).thenReturn(Optional.of(productEntity));

        Product result = productService.getProductById("100");

        assertEquals("Test Product", result.getProdName());
        verify(productRepository, times(1)).findById("100");
    }

    @Test
    void getProductById_ShouldReturnNotFound() {
        String productId = "123";

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.getProductById(productId));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void deleteProduct_shouldRemoveProduct_whenIdExists() {
        when(productRepository.existsById("1")).thenReturn(true);

        productService.deleteProduct("1");

        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void updateProduct_shouldUpdateAndReturnUpdatedProduct() {
        Product product = new Product("300", "Updated Product", 120.0, LocalDate.now());

        ProductEntity existingEntity = new ProductEntity("300","Old Product", 150.0, LocalDate.now(), null, null);

        when(productRepository.findById("300")).thenReturn(Optional.of(existingEntity));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct("300", product);

        assertEquals("Updated Product", result.getProdName());
        assertEquals(120, result.getProdPrice());
        verify(productRepository, times(1)).findById("300");
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void testSearchProductsByName() {
        ProductEntity productEntity = new ProductEntity("400","Test Product", 400.0, LocalDate.now(), null, null);

        when(productRepository.findByNameRegex("Test")).thenReturn(List.of(productEntity));

        List<Product> result = productService.searchProductsByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getProdName());
        verify(productRepository, times(1)).findByNameRegex("Test");
    }

    @Test
    void testFindProductsByPriceRange() {
        Double minPrice = 50.0;
        Double maxPrice = 150.0;
        ProductEntity productEntity = new ProductEntity("500","Test Product", 100.0, LocalDate.now(), null, null);

        when(productRepository.findByProdPriceBetween(minPrice, maxPrice)).thenReturn(List.of(productEntity));

        List<Product> result = productService.findProductsByPriceRange(minPrice, maxPrice);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getProdPrice());
        verify(productRepository, times(1)).findByProdPriceBetween(minPrice, maxPrice);
    }

    @Test
    void testToDTO() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProdName("Entity Name");

        Product product = productService.toDTO(productEntity);

        assertNotNull(product);
        assertEquals("Entity Name", product.getProdName());
    }

    @Test
    void testToEntity() {
        Product product = new Product("1", "Test Product", 100.0, LocalDate.now());

        ProductEntity productEntity = productService.toEntity(product);

        assertNotNull(productEntity);
        assertEquals("Test Product", productEntity.getProdName());
    }
}

