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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request
            ) {

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders() {
        return ResponseEntity.ok(
                orderService.getUserOrders()
        );
    }

    @PatchMapping("orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
            ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderId, request)
        );
    }

    @PatchMapping("order/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}
