package com.pulsedrive.service;

import com.pulsedrive.dto.CartItemRequestDTO;
import com.pulsedrive.entity.Cart;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.repository.CartItemRepository;
import com.pulsedrive.repository.CartRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Vehicle vehicle;
    private Cart cart;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(4L);
        user.setEmail("customer@example.com");

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("BMW");
        vehicle.setModel("M4");
        vehicle.setPrice(14800000.0);
        vehicle.setStock(2);
        vehicle.setAvailable(true);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
    }

    @Test
    void shouldRejectUnavailableVehicle() {

        vehicle.setAvailable(false);

        when(userRepository.findByEmail(
                "customer@example.com"
        )).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        CartItemRequestDTO dto =
                new CartItemRequestDTO();

        dto.setVehicleId(1L);
        dto.setQuantity(1);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> cartService.addToCart(
                                "customer@example.com",
                                dto
                        )
                );

        assertTrue(
                exception.getMessage()
                        .toLowerCase()
                        .contains("unavailable")
        );
    }

    @Test
    void shouldRejectQuantityGreaterThanStock() {

        when(userRepository.findByEmail(
                "customer@example.com"
        )).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        CartItemRequestDTO dto =
                new CartItemRequestDTO();

        dto.setVehicleId(1L);
        dto.setQuantity(5);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> cartService.addToCart(
                                "customer@example.com",
                                dto
                        )
                );

        assertTrue(
                exception.getMessage()
                        .toLowerCase()
                        .contains("stock")
        );
    }
}