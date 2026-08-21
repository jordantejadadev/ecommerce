package com.jordan.ecommerce.dto.auth;

public record LoginRequest(
        String email,
        String password
) { }
