package com.pulsedrive.service;

import com.pulsedrive.dto.CartItemRequestDTO;
import com.pulsedrive.dto.CartItemResponseDTO;
import com.pulsedrive.dto.CartResponseDTO;

import com.pulsedrive.entity.Cart;
import com.pulsedrive.entity.CartItem;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.CartItemRepository;
import com.pulsedrive.repository.CartRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ==========================================
    // ADD VEHICLE TO CART
    // ==========================================
    public CartResponseDTO addToCart(
            String email,
            CartItemRequestDTO dto) {

        User user = getUser(email);

        Vehicle vehicle = vehicleRepository
                .findById(dto.getVehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: "
                                        + dto.getVehicleId()
                        )
                );

        int quantity =
                dto.getQuantity() == null
                        ? 1
                        : dto.getQuantity();

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        if (!Boolean.TRUE.equals(vehicle.getAvailable())) {
            throw new IllegalArgumentException(
                    "Vehicle is currently unavailable"
            );
        }

        if (vehicle.getStock() == null ||
                quantity > vehicle.getStock()) {

            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        Cart cart = getOrCreateCart(user);

        Optional<CartItem> existingItem =
                cartItemRepository
                        .findByCartIdAndVehicleId(
                                cart.getId(),
                                vehicle.getId()
                        );

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            int newQuantity =
                    item.getQuantity() + quantity;

            if (newQuantity > vehicle.getStock()) {
                throw new IllegalArgumentException(
                        "Requested quantity exceeds available stock"
                );
            }

            item.setQuantity(newQuantity);

            cartItemRepository.save(item);

        } else {

            CartItem item = new CartItem();

            item.setCart(cart);
            item.setVehicle(vehicle);
            item.setQuantity(quantity);
            item.setPrice(vehicle.getPrice());

            cartItemRepository.save(item);
        }

        return buildCartResponse(cart);
    }


    // ==========================================
    // GET CURRENT USER CART
    // ==========================================
    public CartResponseDTO getCart(String email) {

        User user = getUser(email);

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (cart == null) {

            CartResponseDTO response =
                    new CartResponseDTO();

            response.setCartId(null);
            response.setItems(List.of());
            response.setTotalItems(0);
            response.setTotalAmount(0.0);

            return response;
        }

        return buildCartResponse(cart);
    }


    // ==========================================
    // UPDATE CART ITEM QUANTITY
    // ==========================================
    public CartResponseDTO updateQuantity(
            String email,
            Long vehicleId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        User user = getUser(email);

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );

        CartItem item = cartItemRepository
                .findByCartIdAndVehicleId(
                        cart.getId(),
                        vehicleId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found in cart"
                        )
                );

        Vehicle vehicle = item.getVehicle();

        if (vehicle.getStock() == null ||
                quantity > vehicle.getStock()) {

            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        item.setQuantity(quantity);

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }


    // ==========================================
    // REMOVE VEHICLE FROM CART
    // ==========================================
    public void removeFromCart(
            String email,
            Long vehicleId) {

        User user = getUser(email);

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );

        CartItem item = cartItemRepository
                .findByCartIdAndVehicleId(
                        cart.getId(),
                        vehicleId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found in cart"
                        )
                );

        cartItemRepository.delete(item);
    }


    // ==========================================
    // GET USER BY EMAIL
    // ==========================================
    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }


    // ==========================================
    // GET EXISTING CART OR CREATE NEW CART
    // ==========================================
    private Cart getOrCreateCart(User user) {

        return cartRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }


    // ==========================================
    // BUILD COMPLETE CART RESPONSE
    // ==========================================
    private CartResponseDTO buildCartResponse(
            Cart cart) {

        List<CartItem> cartItems =
                cartItemRepository
                        .findByCartId(cart.getId());

        List<CartItemResponseDTO> itemDTOs =
                cartItems
                        .stream()
                        .map(this::toItemDTO)
                        .toList();

        int totalItems =
        cartItems.stream()
                .mapToInt(item ->
                        item.getQuantity() != null
                                ? item.getQuantity()
                                : 0
                )
                .sum();

        double totalAmount =
                cartItems
                        .stream()
                        .mapToDouble(item ->
                                item.getPrice()
                                        * item.getQuantity()
                        )
                        .sum();

        CartResponseDTO response =
                new CartResponseDTO();

        response.setCartId(cart.getId());
        response.setItems(itemDTOs);
        response.setTotalItems(totalItems);
        response.setTotalAmount(totalAmount);

        return response;
    }


    // ==========================================
    // CART ITEM ENTITY -> DTO
    // ==========================================
    private CartItemResponseDTO toItemDTO(
            CartItem item) {

        Vehicle vehicle = item.getVehicle();

        CartItemResponseDTO dto =
                new CartItemResponseDTO();

        dto.setCartItemId(item.getId());

        dto.setVehicleId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setVariant(vehicle.getVariant());

        dto.setQuantity(item.getQuantity());

        dto.setUnitPrice(item.getPrice());

        dto.setSubtotal(
                item.getPrice()
                        * item.getQuantity()
        );

        return dto;
    }
}