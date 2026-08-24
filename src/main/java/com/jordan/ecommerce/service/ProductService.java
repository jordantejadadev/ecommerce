package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.product.ProductRequest;
import com.jordan.ecommerce.dto.product.ProductResponse;
import com.jordan.ecommerce.entity.Category;
import com.jordan.ecommerce.entity.Product;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.CategoryRepository;
import com.jordan.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getActive()
        );
    }

    @Transactional
    public ProductResponse updateProduct(
            UUID id,
            ProductRequest request
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        product.setActive(false);
    }
}
