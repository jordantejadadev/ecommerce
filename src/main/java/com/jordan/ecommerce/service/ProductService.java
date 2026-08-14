package com.jordan.ecommerce.service;

import com.jordan.ecommerce.entity.Product;
import com.jordan.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
