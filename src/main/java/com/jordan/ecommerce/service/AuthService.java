package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.auth.LoginRequest;
import com.jordan.ecommerce.dto.auth.LoginResponse;
import com.jordan.ecommerce.dto.refresh.RefreshRequest;
import com.jordan.ecommerce.dto.refresh.RefreshResponse;
import com.jordan.ecommerce.entity.RefreshToken;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.InvalidCredentialsException;
import com.jordan.ecommerce.repository.UserRepository;
import com.jordan.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token,
                refreshToken.getToken()
        );
    }

    public RefreshResponse refresh(RefreshRequest request) {
        RefreshToken storedToken = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.verifyExpiration(storedToken);

        String newAccessToken = jwtService.generateToken(storedToken.getUser());

        return new RefreshResponse(newAccessToken);
    }

    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        return (User) authentication.getPrincipal();
    }
}
