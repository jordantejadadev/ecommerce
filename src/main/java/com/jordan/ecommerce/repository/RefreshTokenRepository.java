package com.jordan.ecommerce.repository;

import com.jordan.ecommerce.entity.RefreshToken;
import com.jordan.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
    void  deleteByUser(User user);
}
