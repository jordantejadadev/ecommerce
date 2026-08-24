package com.jordan.ecommerce.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddress {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
