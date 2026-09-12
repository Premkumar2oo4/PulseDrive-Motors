package com.pulsedrive.repository;

import com.pulsedrive.entity.Wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository
        extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndVehicleId(
            Long userId,
            Long vehicleId
    );

    boolean existsByUserIdAndVehicleId(
            Long userId,
            Long vehicleId
    );
}