package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.cart.AddCartItemRequest;
import com.jordan.ecommerce.dto.cart.CartItemResponse;
import com.jordan.ecommerce.dto.cart.CartResponse;
import com.jordan.ecommerce.entity.Cart;
import com.jordan.ecommerce.entity.CartItem;
import com.jordan.ecommerce.entity.Product;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.InsuficientStockException;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.CartItemRepository;
import com.jordan.ecommerce.repository.CartRepository;
import com.jordan.ecommerce.repository.ProductRepository;
import com.jordan.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse getCartByUserId(UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        return toResponse(cart);
    }

    @Transactional
    public CartResponse addProductToCart(
            UUID userId,
            AddCartItemRequest request
    ) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {

            int newQuantity = cartItem.getQuantity() + request.quantity();

            if (newQuantity > product.getStock()) {
                throw new InsuficientStockException("Stock insuficiente");
            }

            cartItem.setQuantity(newQuantity);

        } else {

            if (request.quantity() > product.getStock()) {
                throw new InsuficientStockException("Stock insuficiente");
            }

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();

            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        return toResponse(cart);
    }

    private Cart createCart(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(cart);
    }

    private CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> {
                    BigDecimal subtotal = item.getProduct()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getPrice(),
                            item.getQuantity(),
                            subtotal
                    );
                }).toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                items,
                total
        );
    }

    @Transactional
    public CartResponse updateCartItem(
            UUID userId,
            UUID productId,
            Integer quantity
    ) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Producto no esta en el carrito"));

        Product product = cartItem.getProduct();

        if (quantity > product.getStock()) {
            throw new RuntimeException("Stock insuficiente");
        }

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeCartItem(
            UUID userId,
            UUID productId
    ) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Carrito no encontrado"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no esta en el carrito"));

        cart.getItems().remove(cartItem);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        cart.getItems().clear();

        return toResponse(cart);
    }
}
