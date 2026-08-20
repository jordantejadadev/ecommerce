package com.jordan.ecommerce.dto.address;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
        @NotBlank
        String street,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        String postalCode,

        @NotBlank
        String country
) { }
