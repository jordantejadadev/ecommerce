package com.jordan.ecommerce.dto.user;

import com.jordan.ecommerce.entity.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) { }
