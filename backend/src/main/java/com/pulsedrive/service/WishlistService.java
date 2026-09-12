package com.pulsedrive.service;

import com.pulsedrive.dto.WishlistResponseDTO;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.Wishlist;
import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;
import com.pulsedrive.repository.WishlistRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public WishlistResponseDTO addToWishlist(
            String email,
            Long vehicleId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Vehicle vehicle = vehicleRepository
                .findById(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: "
                                        + vehicleId
                        )
                );

        if (wishlistRepository
                .existsByUserIdAndVehicleId(
                        user.getId(),
                        vehicleId
                )) {

            throw new DuplicateResourceException(
                    "Vehicle already exists in wishlist"
            );
        }

        Wishlist wishlist = new Wishlist();

        wishlist.setUser(user);
        wishlist.setVehicle(vehicle);

        Wishlist saved =
                wishlistRepository.save(wishlist);

        return toDTO(saved);
    }

    public List<WishlistResponseDTO> getWishlist(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return wishlistRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void removeFromWishlist(
            String email,
            Long vehicleId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Wishlist wishlist =
                wishlistRepository
                        .findByUserIdAndVehicleId(
                                user.getId(),
                                vehicleId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vehicle not found in wishlist"
                                )
                        );

        wishlistRepository.delete(wishlist);
    }

    private WishlistResponseDTO toDTO(
            Wishlist wishlist) {

        Vehicle vehicle = wishlist.getVehicle();

        WishlistResponseDTO dto =
                new WishlistResponseDTO();

        dto.setWishlistId(wishlist.getId());
        dto.setVehicleId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setVariant(vehicle.getVariant());
        dto.setPrice(vehicle.getPrice());

        return dto;
    }
}