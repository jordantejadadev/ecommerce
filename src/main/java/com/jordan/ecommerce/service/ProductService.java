package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.product.ProductRequest;
import com.jordan.ecommerce.dto.product.ProductResponse;
import com.jordan.ecommerce.entity.Category;
import com.jordan.ecommerce.entity.Product;
import com.jordan.ecommerce.entity.ProductImage;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.CategoryRepository;
import com.jordan.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> getAllProducts(Pageable pageable, UUID categoryId) {

        Page<Product> products = (categoryId != null)
                ? productRepository.findAllByActiveTrueAndCategoryId(categoryId, pageable)
                : productRepository.findAllByActiveTrue(pageable);

        return products.map(this::toResponse);
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .category(category)
                .build();

        addImages(product, request.images()); // <-- nuevo

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
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

        product.getImages().clear();
        addImages(product, request.images());

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    private void addImages(Product product, List<String> imageUrls) {
        if (imageUrls == null) return;

        imageUrls.forEach(url -> {
            ProductImage image = ProductImage.builder()
                    .url(url)
                    .product(product)
                    .build();
            product.getImages().add(image);
        });
    }

    private ProductResponse toResponse(Product product) {
        List<String> imageUrls = product.getImages()
                .stream()
                .map(ProductImage::getUrl)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                imageUrls,
                product.getCategory().getId(),
                product.getActive()
        );
    }

    @Transactional
    public void deleteProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        product.setActive(false);
    }
}
