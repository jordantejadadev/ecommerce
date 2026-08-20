package com.jordan.ecommerce.dto.cart;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) { }
