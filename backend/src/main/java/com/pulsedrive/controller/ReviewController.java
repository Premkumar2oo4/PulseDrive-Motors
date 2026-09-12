package com.pulsedrive.controller;

import com.pulsedrive.dto.ReviewRequestDTO;
import com.pulsedrive.dto.ReviewResponseDTO;
import com.pulsedrive.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO>
    createReview(
            Principal principal,
           @Valid @RequestBody ReviewRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reviewService.createReview(
                                principal.getName(),
                                dto
                        )
                );
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ReviewResponseDTO>>
    getVehicleReviews(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                reviewService
                        .getVehicleReviews(vehicleId)
        );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO>
    updateReview(
            Principal principal,
            @PathVariable Long reviewId,
           @Valid @RequestBody ReviewRequestDTO dto) {

        return ResponseEntity.ok(
                reviewService.updateReview(
                        principal.getName(),
                        reviewId,
                        dto
                )
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void>
    deleteReview(
            Principal principal,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(
                principal.getName(),
                reviewId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}