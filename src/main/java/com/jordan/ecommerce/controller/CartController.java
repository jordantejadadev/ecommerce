package com.jordan.ecommerce.controller;

import com.jordan.ecommerce.dto.cart.AddCartItemRequest;
import com.jordan.ecommerce.dto.cart.CartResponse;
import com.jordan.ecommerce.dto.cart.UpdateCartItemRequest;
import com.jordan.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable UUID userId
            ) {
        return ResponseEntity.ok(
                cartService.getCartByUserId(userId)
        );
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addProduct(
            @PathVariable UUID userId,
            @Valid @RequestBody AddCartItemRequest request
            ) {
        return ResponseEntity.ok(
                cartService.addProductToCart(userId, request)
        );
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateCartItemRequest request
            ) {
        return ResponseEntity.ok(
                cartService.updateCartItem(
                        userId,
                        productId,
                        request.quantity()
                )
        );
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable UUID userId,
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(
                cartService.removeCartItem(userId, productId)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartResponse> clearCart(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                cartService.clearCart(userId)
        );
    }
}
