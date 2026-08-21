package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.category.CategoryRequest;
import com.jordan.ecommerce.dto.category.CategoryResponse;
import com.jordan.ecommerce.entity.Category;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

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
}
