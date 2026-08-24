package com.jordan.ecommerce.dto.order;

public record OrderAddressResponse(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) { }
