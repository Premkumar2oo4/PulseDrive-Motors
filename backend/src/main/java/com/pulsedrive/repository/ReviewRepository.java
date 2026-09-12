package com.pulsedrive.repository;

import com.pulsedrive.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByVehicleIdOrderByCreatedAtDesc(
            Long vehicleId
    );

    Optional<Review> findByUserIdAndVehicleId(
            Long userId,
            Long vehicleId
    );

    boolean existsByUserIdAndVehicleId(
            Long userId,
            Long vehicleId
    );
}