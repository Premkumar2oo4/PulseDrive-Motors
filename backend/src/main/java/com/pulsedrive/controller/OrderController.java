package com.pulsedrive.controller;
import jakarta.validation.Valid;
import com.pulsedrive.dto.CheckoutRequestDTO;
import com.pulsedrive.dto.OrderResponseDTO;
import com.pulsedrive.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService) {

        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            Principal principal,
           @Valid @RequestBody CheckoutRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderService.checkout(
                                principal.getName(),
                                dto
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
            Principal principal) {

        return ResponseEntity.ok(
                orderService.getMyOrders(
                        principal.getName()
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            Principal principal,
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(
                        principal.getName(),
                        orderId
                )
        );
    }
    @PutMapping("/{orderId}/cancel")
public ResponseEntity<OrderResponseDTO> cancelOrder(
        Principal principal,
        @PathVariable Long orderId) {

    return ResponseEntity.ok(
            orderService.cancelOrder(
                    principal.getName(),
                    orderId
            )
    );
}
}