package com.pulsedrive.controller;

import com.pulsedrive.dto.WishlistResponseDTO;
import com.pulsedrive.service.WishlistService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService) {

        this.wishlistService = wishlistService;
    }

    @PostMapping("/{vehicleId}")
    public ResponseEntity<WishlistResponseDTO> addToWishlist(
            Principal principal,
            @PathVariable Long vehicleId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        wishlistService.addToWishlist(
                                principal.getName(),
                                vehicleId
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponseDTO>> getWishlist(
            Principal principal) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(
                        principal.getName()
                )
        );
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> removeFromWishlist(
            Principal principal,
            @PathVariable Long vehicleId) {

        wishlistService.removeFromWishlist(
                principal.getName(),
                vehicleId
        );

        return ResponseEntity.noContent().build();
    }
}