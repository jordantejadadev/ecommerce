package com.jordan.ecommerce.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name
) { }
