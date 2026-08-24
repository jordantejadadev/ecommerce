package com.jordan.ecommerce.repository;

import com.jordan.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndActiveTrue(UUID id);

    List<Product> findAllByActiveTrue();

    boolean existsByCategoryId(UUID categoryId);
}
