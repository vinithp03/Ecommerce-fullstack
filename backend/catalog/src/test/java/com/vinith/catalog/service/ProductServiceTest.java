package com.vinith.catalog.service;

import com.vinith.catalog.DtoLayer.ProductResponse;
import com.vinith.catalog.EntityLayer.Product;
import com.vinith.catalog.Exceptions.ProductCreationException;
import com.vinith.catalog.RepositoryLayer.ProductRepository;
import com.vinith.catalog.ServiceLayer.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnProductWhenValidId() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU001");
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse result = productService.getByIdAsDto(1L);

        // Assert
        assertNotNull(result);
        assertEquals("SKU001", result.getSku());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductCreationException.class, () -> {
            productService.getByIdAsDto(99L);
        });
    }
}
