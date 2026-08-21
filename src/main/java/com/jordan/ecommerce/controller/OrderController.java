package com.jordan.ecommerce.controller;

import com.jordan.ecommerce.dto.order.CreateOrderRequest;
import com.jordan.ecommerce.dto.order.OrderResponse;
import com.jordan.ecommerce.dto.orderStatus.UpdateOrderStatusRequest;
import com.jordan.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateOrderRequest request
            ) {

        OrderResponse response = orderService.createOrder(
                userId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                orderService.getUserOrders(userId)
        );
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
            ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        userId,
                        orderId,
                        request
                )
        );
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID userId,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, userId)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID userId,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(userId, orderId)
        );
    }
}
