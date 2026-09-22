package com.jordan.ecommerce.controller;

import com.jordan.ecommerce.dto.order.AdminOrderResponse;
import com.jordan.ecommerce.dto.orderStatus.UpdateOrderStatusRequest;
import com.jordan.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
            ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatusAsAdmin(orderId, request)
        );
    }
}
