package com.jordan.ecommerce.dto.auth;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String name,
        String email,
        String role,
        String token
) { }
