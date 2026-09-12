package com.pulsedrive.controller;

import com.pulsedrive.dto.CartItemRequestDTO;
import com.pulsedrive.dto.CartResponseDTO;
import com.pulsedrive.service.CartService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService) {

        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> addToCart(
            Principal principal,
           @Valid @RequestBody CartItemRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cartService.addToCart(
                                principal.getName(),
                                dto
                        )
                );
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            Principal principal) {

        return ResponseEntity.ok(
                cartService.getCart(
                        principal.getName()
                )
        );
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            Principal principal,
            @PathVariable Long vehicleId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        principal.getName(),
                        vehicleId,
                        quantity
                )
        );
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> removeFromCart(
            Principal principal,
            @PathVariable Long vehicleId) {

        cartService.removeFromCart(
                principal.getName(),
                vehicleId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}