package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.category.CategoryRequest;
import com.jordan.ecommerce.dto.category.CategoryResponse;
import com.jordan.ecommerce.entity.Category;
import com.jordan.ecommerce.exception.CategoryNotEmptyException;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return toResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = Category.builder()
                .name(request.name())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    @Transactional
    public CategoryResponse updateCategory(
            UUID id,
            CategoryRequest request
    ) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        category.setName(request.name());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (productRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmptyException(
                    "No se puede eliminar la categoria porque tiene productos asociados"
            );
        }

        categoryRepository.delete(category);
    }
}
