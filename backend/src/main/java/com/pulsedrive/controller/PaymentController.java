package com.pulsedrive.controller;

import com.pulsedrive.dto.PaymentResponseDTO;
import com.pulsedrive.dto.RazorpayOrderResponseDTO;
import com.pulsedrive.dto.RazorpayVerifyRequestDTO;

import com.pulsedrive.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService =
                paymentService;
    }

    // CREATE RAZORPAY ORDER
    @PostMapping("/razorpay/order/{orderId}")
    public ResponseEntity<RazorpayOrderResponseDTO>
    createRazorpayOrder(
            Principal principal,
            @PathVariable Long orderId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        paymentService
                                .createRazorpayOrder(
                                        principal.getName(),
                                        orderId
                                )
                );
    }


    // VERIFY RAZORPAY PAYMENT
    @PostMapping("/razorpay/verify")
    public ResponseEntity<PaymentResponseDTO>
    verifyRazorpayPayment(
            Principal principal,
           @Valid @RequestBody RazorpayVerifyRequestDTO dto) {

        return ResponseEntity.ok(
                paymentService
                        .verifyRazorpayPayment(
                                principal.getName(),
                                dto
                        )
        );
    }


    // GET PAYMENT
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO>
    getPaymentByOrderId(
            Principal principal,
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService
                        .getPaymentByOrderId(
                                principal.getName(),
                                orderId
                        )
        );
    }
}