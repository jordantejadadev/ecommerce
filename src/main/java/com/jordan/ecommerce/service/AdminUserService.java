package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.user.AdminUserResponse;
import com.jordan.ecommerce.entity.Role;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    public List<AdminUserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminUserResponse updateRole(
            UUID userId,
            Role newRole
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        user.setRole(newRole);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
