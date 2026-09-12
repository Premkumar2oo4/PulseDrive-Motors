package com.pulsedrive.service;

import com.pulsedrive.dto.PaymentResponseDTO;
import com.pulsedrive.dto.RazorpayOrderResponseDTO;
import com.pulsedrive.dto.RazorpayVerifyRequestDTO;

import com.pulsedrive.entity.Order;
import com.pulsedrive.entity.Payment;

import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.OrderRepository;
import com.pulsedrive.repository.PaymentRepository;

import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final OrderRepository orderRepository;

        private final String razorpayKeyId;
        private final String razorpayKeySecret;

        public PaymentService(
                        PaymentRepository paymentRepository,
                        OrderRepository orderRepository,
                        @Value("${razorpay.key.id}") String razorpayKeyId,
                        @Value("${razorpay.key.secret}") String razorpayKeySecret) {

                this.paymentRepository = paymentRepository;
                this.orderRepository = orderRepository;
                this.razorpayKeyId = razorpayKeyId;

                this.razorpayKeySecret = razorpayKeySecret;
        }

        // =====================================================
        // CREATE RAZORPAY ORDER
        // =====================================================
        @Transactional
        public RazorpayOrderResponseDTO createRazorpayOrder(
                        String email,
                        Long orderId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: " + orderId));

                // Ownership check
                if (!order.getUser().getEmail().equals(email)) {
                        throw new IllegalArgumentException(
                                        "You are not allowed to pay for this order");
                }

                if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                        throw new DuplicateResourceException(
                                        "Order is already paid");
                }

                Payment existingPayment = paymentRepository
                                .findByOrderId(orderId)
                                .orElse(null);

                if (existingPayment != null &&
                                "SUCCESS".equalsIgnoreCase(existingPayment.getStatus())) {

                        throw new DuplicateResourceException(
                                        "Payment already completed for order: " + orderId);
                }

                try {

                        RazorpayClient razorpayClient = new RazorpayClient(
                                        razorpayKeyId,
                                        razorpayKeySecret);

                        long amountInPaise = Math.round(
                                        order.getTotalAmount() * 100);

                        JSONObject request = new JSONObject();

                        request.put(
                                        "amount",
                                        amountInPaise);

                        request.put(
                                        "currency",
                                        "INR");

                        request.put(
                                        "receipt",
                                        "order_" + order.getId());

                        com.razorpay.Order razorpayOrder = razorpayClient.orders
                                        .create(request);

                        String razorpayOrderId = razorpayOrder.get("id");

                        Payment payment;

                        if (existingPayment != null) {

                                payment = existingPayment;

                        } else {

                                payment = new Payment();
                                payment.setOrder(order);
                        }

                        payment.setAmount(
                                        order.getTotalAmount());

                        payment.setStatus("CREATED");

                        payment.setPaymentMethod(
                                        "RAZORPAY");

                        payment.setRazorpayOrderId(
                                        razorpayOrderId);

                        paymentRepository.save(payment);

                        RazorpayOrderResponseDTO response = new RazorpayOrderResponseDTO();

                        response.setInternalOrderId(
                                        order.getId());

                        response.setRazorpayOrderId(
                                        razorpayOrderId);

                        response.setAmount(
                                        amountInPaise);

                        response.setCurrency("INR");

                        response.setKeyId(
                                        razorpayKeyId);

                        return response;

                } catch (Exception exception) {

                        throw new IllegalArgumentException(
                                        "Unable to create Razorpay order: "
                                                        + exception.getMessage());
                }
        }

        // =====================================================
        // VERIFY RAZORPAY PAYMENT
        // =====================================================
        @Transactional
        public PaymentResponseDTO verifyRazorpayPayment(
                        String email,
                        RazorpayVerifyRequestDTO dto) {

                Order order = orderRepository
                                .findById(dto.getOrderId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: "
                                                                + dto.getOrderId()));

                if (!order.getUser().getEmail().equals(email)) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to verify this payment");
                }

                Payment payment = paymentRepository
                                .findByOrderId(order.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Payment record not found for order: "
                                                                + order.getId()));

                /*
                 * IMPORTANT:
                 * Verify against the Razorpay order ID stored
                 * by OUR backend, not blindly against the
                 * order ID sent by the browser.
                 */
                if (!payment.getRazorpayOrderId()
                                .equals(dto.getRazorpayOrderId())) {

                        throw new IllegalArgumentException(
                                        "Razorpay order id mismatch");
                }

                try {

                        JSONObject verificationData = new JSONObject();

                        verificationData.put(
                                        "razorpay_order_id",
                                        payment.getRazorpayOrderId());

                        verificationData.put(
                                        "razorpay_payment_id",
                                        dto.getRazorpayPaymentId());

                        verificationData.put(
                                        "razorpay_signature",
                                        dto.getRazorpaySignature());

                        boolean valid = Utils.verifyPaymentSignature(
                                        verificationData,
                                        razorpayKeySecret);

                        if (!valid) {

                                payment.setStatus("FAILED");

                                paymentRepository.save(payment);

                                throw new IllegalArgumentException(
                                                "Invalid Razorpay payment signature");
                        }

                        payment.setStatus("SUCCESS");

                        payment.setRazorpayPaymentId(
                                        dto.getRazorpayPaymentId());

                        payment.setRazorpaySignature(
                                        dto.getRazorpaySignature());

                        Payment savedPayment = paymentRepository.save(payment);

                        order.setPaymentStatus("PAID");
                        order.setStatus("CONFIRMED");

                        orderRepository.save(order);

                        return toResponseDTO(savedPayment);

                } catch (IllegalArgumentException exception) {

                        throw exception;

                } catch (Exception exception) {

                        throw new IllegalArgumentException(
                                        "Payment verification failed: "
                                                        + exception.getMessage());
                }
        }

        // =====================================================
        // GET PAYMENT BY ORDER
        // =====================================================
        public PaymentResponseDTO getPaymentByOrderId(
                        String email,
                        Long orderId) {

                Payment payment = paymentRepository
                                .findByOrderId(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Payment not found for order: "
                                                                + orderId));

                if (!payment.getOrder()
                                .getUser()
                                .getEmail()
                                .equals(email)) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to access this payment");
                }

                return toResponseDTO(payment);
        }

        private PaymentResponseDTO toResponseDTO(Payment payment) {

                PaymentResponseDTO dto = new PaymentResponseDTO();

                // Payment information
                dto.setPaymentId(payment.getId());

                dto.setOrderId(
                                payment.getOrder().getId());

                dto.setAmount(
                                payment.getAmount());

                dto.setStatus(
                                payment.getStatus());

                dto.setPaymentMethod(
                                payment.getPaymentMethod());

                dto.setTransactionId(
                                payment.getTransactionId());

                // Refund information
                dto.setRefundStatus(
                                payment.getRefundStatus());

                dto.setRefundId(
                                payment.getRefundId());

                dto.setRefundedAmount(
                                payment.getRefundedAmount());

                dto.setRefundedAt(
                                payment.getRefundedAt());

                return dto;
        }

        public List<PaymentResponseDTO> getAllPayments() {

                return paymentRepository
                                .findAllByOrderByIdDesc()
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        public PaymentResponseDTO getPaymentById(
                        Long paymentId) {

                Payment payment = paymentRepository
                                .findById(paymentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Payment not found with id: "
                                                                + paymentId));

                return toResponseDTO(payment);
        }

        public List<PaymentResponseDTO> getPaymentsByStatus(
                        String status) {

                return paymentRepository
                                .findByStatusIgnoreCase(
                                                status.trim().toUpperCase())
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

       @Transactional
public PaymentResponseDTO refundPayment(
        Long paymentId) {

    Payment payment =
            paymentRepository
                    .findById(paymentId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Payment not found with id: "
                                            + paymentId
                            )
                    );

    if (!"SUCCESS".equalsIgnoreCase(
            payment.getStatus())) {

        throw new IllegalArgumentException(
                "Only successful payments can be refunded"
        );
    }

    if (payment.getRazorpayPaymentId() == null ||
            payment.getRazorpayPaymentId().isBlank()) {

        throw new IllegalArgumentException(
                "Razorpay payment id is missing"
        );
    }

    if ("REFUNDED".equalsIgnoreCase(
            payment.getRefundStatus())) {

        throw new IllegalArgumentException(
                "Payment is already refunded"
        );
    }

    try {

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        razorpayKeyId,
                        razorpayKeySecret
                );

        JSONObject refundRequest =
                new JSONObject();

        /*
         * Full refund:
         * omit amount OR send full amount in paise.
         *
         * We'll explicitly send the full amount.
         */
        long amountInPaise =
                Math.round(
                        payment.getAmount() * 100
                );

        refundRequest.put(
                "amount",
                amountInPaise
        );

        com.razorpay.Refund razorpayRefund =
                razorpayClient
                        .payments
                        .refund(
                                payment.getRazorpayPaymentId(),
                                refundRequest
                        );

        String refundId =
                razorpayRefund.get("id");

        payment.setRefundStatus(
                "REFUNDED"
        );

        payment.setRefundId(
                refundId
        );

        payment.setRefundedAmount(
                payment.getAmount()
        );

        payment.setRefundedAt(
                LocalDateTime.now()
        );

        Order order =
                payment.getOrder();

        order.setPaymentStatus(
                "REFUNDED"
        );

        paymentRepository.save(payment);
        orderRepository.save(order);

        return toResponseDTO(payment);

    } catch (Exception exception) {

        throw new IllegalArgumentException(
                "Razorpay refund failed: "
                        + exception.getMessage()
        );
    }
}
}