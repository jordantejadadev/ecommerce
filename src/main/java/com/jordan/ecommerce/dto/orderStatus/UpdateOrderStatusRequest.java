package com.jordan.ecommerce.dto.orderStatus;

import com.jordan.ecommerce.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatus status
) { }
