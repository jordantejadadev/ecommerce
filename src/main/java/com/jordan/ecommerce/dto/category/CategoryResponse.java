package com.jordan.ecommerce.dto.category;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) { }
