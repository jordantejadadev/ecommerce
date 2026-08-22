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

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(
                cartService.getCart()
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addProduct(
            @Valid @RequestBody AddCartItemRequest request
            ) {
        return ResponseEntity.ok(
                cartService.addProductToCart(request)
        );
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateCartItemRequest request
            ) {
        return ResponseEntity.ok(
                cartService.updateCartItem(
                        productId,
                        request.quantity()
                )
        );
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(
                cartService.removeCartItem(productId)
        );
    }

    @DeleteMapping()
    public ResponseEntity<CartResponse> clearCart() {
        return ResponseEntity.ok(
                cartService.clearCart()
        );
    }
}
