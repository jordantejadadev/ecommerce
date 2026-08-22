package com.jordan.ecommerce.controller;

import com.jordan.ecommerce.dto.address.AddressResponse;
import com.jordan.ecommerce.dto.address.CreateAddressRequest;
import com.jordan.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody CreateAddressRequest request
            ) {
        AddressResponse response = addressService.createAddress(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        return ResponseEntity.ok(
                addressService.getUserAddresses()
        );
    }
}
