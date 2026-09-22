package com.jordan.ecommerce.dto.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        List<String> images,
        UUID categoryId,
        Boolean active
) { }