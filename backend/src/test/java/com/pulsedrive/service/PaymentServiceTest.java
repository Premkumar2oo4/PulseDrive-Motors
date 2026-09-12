package com.pulsedrive.service;

import com.pulsedrive.dto.PaymentResponseDTO;

import com.pulsedrive.entity.Order;
import com.pulsedrive.entity.Payment;
import com.pulsedrive.entity.User;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.OrderRepository;
import com.pulsedrive.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;

    private PaymentService paymentService;

    private User user;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {

        paymentRepository =
                mock(PaymentRepository.class);

        orderRepository =
                mock(OrderRepository.class);

        /*
         * PaymentService also needs Razorpay keys.
         * We use dummy values because these tests
         * do NOT call the real Razorpay API.
         */
        paymentService =
                new PaymentService(
                        paymentRepository,
                        orderRepository,
                        "rzp_test_dummy",
                        "dummy_secret"
                );

        // =========================
        // USER
        // =========================

        user = new User();

        ReflectionTestUtils.setField(
                user,
                "id",
                4L
        );

        user.setEmail(
                "customer@example.com"
        );

        // =========================
        // ORDER
        // =========================

        order = new Order();

        ReflectionTestUtils.setField(
                order,
                "id",
                10L
        );

        order.setUser(user);
        order.setTotalAmount(50000.0);
        order.setStatus("CONFIRMED");
        order.setPaymentStatus("PAID");

        // =========================
        // PAYMENT
        // =========================

        payment = new Payment();

        ReflectionTestUtils.setField(
                payment,
                "id",
                5L
        );

        payment.setOrder(order);
        payment.setAmount(50000.0);
        payment.setStatus("SUCCESS");
        payment.setPaymentMethod("RAZORPAY");

        payment.setRazorpayOrderId(
                "order_test_123"
        );

        payment.setRazorpayPaymentId(
                "pay_test_123"
        );
    }

    // =========================================
    // TEST 1 - GET PAYMENT BY ID
    // =========================================

    @Test
    void shouldGetPaymentByIdSuccessfully() {

        when(
                paymentRepository.findById(5L)
        ).thenReturn(
                Optional.of(payment)
        );

        PaymentResponseDTO response =
                paymentService.getPaymentById(5L);

        assertNotNull(response);

        assertEquals(
                5L,
                response.getPaymentId()
        );

        assertEquals(
                10L,
                response.getOrderId()
        );

        assertEquals(
                50000.0,
                response.getAmount()
        );

        assertEquals(
                "SUCCESS",
                response.getStatus()
        );
    }

    // =========================================
    // TEST 2 - PAYMENT NOT FOUND
    // =========================================

    @Test
    void shouldThrowWhenPaymentDoesNotExist() {

        when(
                paymentRepository.findById(99L)
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                paymentService
                                        .getPaymentById(99L)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Payment not found")
        );
    }

    // =========================================
    // TEST 3 - GET ALL PAYMENTS
    // =========================================

    @Test
    void shouldReturnAllPayments() {

        when(
                paymentRepository
                        .findAllByOrderByIdDesc()
        ).thenReturn(
                List.of(payment)
        );

        List<PaymentResponseDTO> responses =
                paymentService.getAllPayments();

        assertEquals(
                1,
                responses.size()
        );

        assertEquals(
                5L,
                responses.get(0)
                        .getPaymentId()
        );
    }

    // =========================================
    // TEST 4 - FILTER PAYMENT BY STATUS
    // =========================================

    @Test
    void shouldReturnPaymentsByStatus() {

        when(
                paymentRepository
                        .findByStatusIgnoreCase(
                                "SUCCESS"
                        )
        ).thenReturn(
                List.of(payment)
        );

        List<PaymentResponseDTO> responses =
                paymentService
                        .getPaymentsByStatus(
                                "success"
                        );

        assertEquals(
                1,
                responses.size()
        );

        assertEquals(
                "SUCCESS",
                responses.get(0)
                        .getStatus()
        );
    }

    // =========================================
    // TEST 5 - REFUND FAILED PAYMENT
    // =========================================

    @Test
    void shouldRejectRefundForNonSuccessfulPayment() {

        payment.setStatus("CREATED");

        when(
                paymentRepository.findById(5L)
        ).thenReturn(
                Optional.of(payment)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                paymentService
                                        .refundPayment(5L)
                );

        assertEquals(
                "Only successful payments can be refunded",
                exception.getMessage()
        );
    }

    // =========================================
    // TEST 6 - MISSING RAZORPAY PAYMENT ID
    // =========================================

    @Test
    void shouldRejectRefundWhenRazorpayPaymentIdMissing() {

        payment.setStatus("SUCCESS");

        payment.setRazorpayPaymentId(null);

        when(
                paymentRepository.findById(5L)
        ).thenReturn(
                Optional.of(payment)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                paymentService
                                        .refundPayment(5L)
                );

        assertEquals(
                "Razorpay payment id is missing",
                exception.getMessage()
        );
    }

    // =========================================
    // TEST 7 - ALREADY REFUNDED
    // =========================================

    @Test
    void shouldRejectAlreadyRefundedPayment() {

        payment.setStatus("SUCCESS");

        payment.setRazorpayPaymentId(
                "pay_test_123"
        );

        payment.setRefundStatus(
                "REFUNDED"
        );

        when(
                paymentRepository.findById(5L)
        ).thenReturn(
                Optional.of(payment)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                paymentService
                                        .refundPayment(5L)
                );

        assertEquals(
                "Payment is already refunded",
                exception.getMessage()
        );
    }
}