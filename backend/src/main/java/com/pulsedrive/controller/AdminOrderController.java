package com.pulsedrive.controller;

import com.pulsedrive.dto.OrderResponseDTO;
import com.pulsedrive.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(
            OrderService orderService) {

        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>>
    getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO>
    updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        status
                )
        );
    }
}