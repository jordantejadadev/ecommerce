package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.user.UserResponse;
import com.jordan.ecommerce.entity.Role;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(
            String name,
            String email,
            String password
    ) {

        if (userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("El email ya está registrado");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public User getUserById(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

}
