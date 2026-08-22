package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.address.AddressResponse;
import com.jordan.ecommerce.dto.address.CreateAddressRequest;
import com.jordan.ecommerce.entity.Address;
import com.jordan.ecommerce.entity.User;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.AddressRepository;
import com.jordan.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AuthService authService;

    public AddressResponse createAddress(
            CreateAddressRequest request
    ) {

        User user = authService.getAuthenticatedUser();

        Address address = Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .postalCode(request.postalCode())
                .country(request.country())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);

        return toResponse(savedAddress);
    }

    public List<AddressResponse> getUserAddresses() {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}
