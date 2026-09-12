package com.pulsedrive.service;

import com.pulsedrive.dto.ReviewRequestDTO;
import com.pulsedrive.dto.ReviewResponseDTO;

import com.pulsedrive.entity.Review;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;

import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.ReviewRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public ReviewResponseDTO createReview(
            String email,
            ReviewRequestDTO dto) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Vehicle vehicle = vehicleRepository
                .findById(dto.getVehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: "
                                        + dto.getVehicleId()
                        )
                );

        validateRating(dto.getRating());

        if (reviewRepository
                .existsByUserIdAndVehicleId(
                        user.getId(),
                        vehicle.getId()
                )) {

            throw new DuplicateResourceException(
                    "You have already reviewed this vehicle"
            );
        }

        Review review = new Review();

        review.setUser(user);
        review.setVehicle(vehicle);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved =
                reviewRepository.save(review);

        return toDTO(saved);
    }

    public List<ReviewResponseDTO> getVehicleReviews(
            Long vehicleId) {

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException(
                    "Vehicle not found with id: " + vehicleId
            );
        }

        return reviewRepository
                .findByVehicleIdOrderByCreatedAtDesc(
                        vehicleId
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ReviewResponseDTO updateReview(
            String email,
            Long reviewId,
            ReviewRequestDTO dto) {

        Review review =
                getReview(reviewId);

        if (!review.getUser()
                .getEmail()
                .equals(email)) {

            throw new IllegalArgumentException(
                    "You are not allowed to update this review"
            );
        }

        validateRating(dto.getRating());

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updated =
                reviewRepository.save(review);

        return toDTO(updated);
    }

    public void deleteReview(
            String email,
            Long reviewId) {

        Review review =
                getReview(reviewId);

        if (!review.getUser()
                .getEmail()
                .equals(email)) {

            throw new IllegalArgumentException(
                    "You are not allowed to delete this review"
            );
        }

        reviewRepository.delete(review);
    }

    private void validateRating(Integer rating) {

        if (rating == null ||
                rating < 1 ||
                rating > 5) {

            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5"
            );
        }
    }

    private Review getReview(Long id) {

        return reviewRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: " + id
                        )
                );
    }

    private ReviewResponseDTO toDTO(
            Review review) {

        ReviewResponseDTO dto =
                new ReviewResponseDTO();

        dto.setId(review.getId());

        dto.setUserId(
                review.getUser().getId()
        );

        dto.setUserName(
                review.getUser().getFirstName()
                        + " "
                        + review.getUser().getLastName()
        );

        dto.setVehicleId(
                review.getVehicle().getId()
        );

        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        dto.setCreatedAt(
                review.getCreatedAt()
        );

        dto.setUpdatedAt(
                review.getUpdatedAt()
        );

        return dto;
    }
}