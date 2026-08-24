package com.jordan.ecommerce.service;

import com.jordan.ecommerce.dto.order.CreateOrderRequest;
import com.jordan.ecommerce.dto.order.OrderAddressResponse;
import com.jordan.ecommerce.dto.order.OrderItemResponse;
import com.jordan.ecommerce.dto.order.OrderResponse;
import com.jordan.ecommerce.dto.orderStatus.UpdateOrderStatusRequest;
import com.jordan.ecommerce.entity.*;
import com.jordan.ecommerce.exception.EmptyCartException;
import com.jordan.ecommerce.exception.InsufficientStockException;
import com.jordan.ecommerce.exception.InvalidOrderStatusException;
import com.jordan.ecommerce.exception.ResourceNotFoundException;
import com.jordan.ecommerce.repository.AddressRepository;
import com.jordan.ecommerce.repository.CartRepository;
import com.jordan.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        // 2. Buscar dirección
        Address address = addressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(()-> new ResourceNotFoundException("Dirección no encontrada"));

        // 3. Buscar carrito
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Carrito no encontrado"));

        // 4. Verificar que el carrito no esté vacío
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("El carrito está vacío");
        }

        OrderAddress orderAddress = OrderAddress.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();

        // 5. Crear Order
        Order order = Order.builder()
                .user(user)
                .address(orderAddress)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // 6. Crear OrderItems y calcular total
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (!product.getActive()) {
                throw new ResourceNotFoundException(
                        "Producto no disponible: " + product.getName()
                );
            }

            if (cartItem.getQuantity() > product.getStock()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            order.getItems().add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());

            total = total.add(subtotal);
        }

        // 7. Guardar total
        order.setTotal(total);

        // 8. Guardar Order
        Order savedOrder = orderRepository.save(order);

        // 9. Vaciar carrito
        cart.getItems().clear();

        return toResponse(savedOrder);
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> {
                    BigDecimal subtotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new OrderItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getUnitPrice(),
                            item.getQuantity(),
                            subtotal
                    );
                })
                .toList();

        OrderAddressResponse address = new OrderAddressResponse(
                order.getAddress().getStreet(),
                order.getAddress().getCity(),
                order.getAddress().getState(),
                order.getAddress().getPostalCode(),
                order.getAddress().getCountry()
        );

        return new OrderResponse(
                order.getId(),
                address,
                order.getStatus(),
                order.getTotal(),
                items,
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getUserOrders() {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse updateOrderStatus(
            UUID orderId,
            UpdateOrderStatusRequest request
    ) {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Orden no encontrada"
                        ));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.status();

        if(!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidOrderStatusException(
                    "Transición de estado no válida"
            );
        }

        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    private boolean isValidTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;

            case PAID -> newStatus == OrderStatus.SHIPPED;

            case SHIPPED -> newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        // 1. Buscar la orden
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // 2. Verificar si se puede cancelar
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStatusException("La orden no puede ser cancelada");
        }

        // 3. Devolver stock
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStock(product.getStock() + item.getQuantity());
        }

        // 4. Cambiar estado
        order.setStatus(OrderStatus.CANCELLED);

        // 5. Guardar orden
        Order savedOrder = orderRepository.save(order);

        // 6. Convertir a response
        return toResponse(savedOrder);

    }

    @Transactional
    public OrderResponse getOrderById(UUID orderId) {

        User user = authService.getAuthenticatedUser();

        UUID userId = user.getId();

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Orden no encontrada"));

        return toResponse(order);
    }

}
