package com.pulsedrive.controller;

import com.pulsedrive.dto.PaymentResponseDTO;
import com.pulsedrive.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;

    public AdminPaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>>
    getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO>
    getPaymentById(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>>
    getPaymentsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status)
        );
    }

    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDTO>
    refundPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                paymentService.refundPayment(paymentId)
        );
    }
}