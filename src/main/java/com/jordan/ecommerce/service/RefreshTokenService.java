package com.jordan.ecommerce.service;

import com.jordan.ecommerce.entity.RefreshToken;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.InvalidCredentialsException;
import com.jordan.ecommerce.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long REFRESH_TOKEN_DURATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 días

    public RefreshToken createRefreshToken(User user) {

        // Un usuario, un refresh token activo a la vez (evita acumular basura en la tabla)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidCredentialsException("Refresh token expirado, inicia sesión de nuevo");
        }

        return token;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Refresh token inválido"));
    }
}
