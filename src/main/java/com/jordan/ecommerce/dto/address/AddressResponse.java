package com.jordan.ecommerce.dto.address;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String city,
        String state,
        String postalCode,
        String country
) { }
