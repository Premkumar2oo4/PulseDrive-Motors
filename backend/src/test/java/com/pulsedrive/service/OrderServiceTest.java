package com.pulsedrive.service;

import com.pulsedrive.dto.CheckoutRequestDTO;
import com.pulsedrive.dto.OrderResponseDTO;

import com.pulsedrive.entity.Cart;
import com.pulsedrive.entity.CartItem;
import com.pulsedrive.entity.Order;
import com.pulsedrive.entity.OrderItem;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;

import com.pulsedrive.repository.CartItemRepository;
import com.pulsedrive.repository.CartRepository;
import com.pulsedrive.repository.CouponUsageRepository;
import com.pulsedrive.repository.OrderItemRepository;
import com.pulsedrive.repository.OrderRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private CouponUsageRepository couponUsageRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Vehicle vehicle;
    private CartItem cartItem;
    private CheckoutRequestDTO checkoutRequest;

    @BeforeEach
    void setUp() {

        user = new User();

        ReflectionTestUtils.setField(
                user,
                "id",
                4L
        );

        user.setEmail(
                "customer@example.com"
        );

        cart = new Cart();

        ReflectionTestUtils.setField(
                cart,
                "id",
                2L
        );

        cart.setUser(user);

        vehicle = new Vehicle();

        ReflectionTestUtils.setField(
                vehicle,
                "id",
                1L
        );

        vehicle.setBrand("BMW");
        vehicle.setModel("M4");
        vehicle.setVariant("Competition");
        vehicle.setPrice(14800000.0);
        vehicle.setStock(3);
        vehicle.setAvailable(true);

        cartItem = new CartItem();

        ReflectionTestUtils.setField(
                cartItem,
                "id",
                1L
        );

        cartItem.setCart(cart);
        cartItem.setVehicle(vehicle);
        cartItem.setQuantity(1);
        cartItem.setPrice(14800000.0);

        checkoutRequest =
                new CheckoutRequestDTO();

        checkoutRequest.setShippingAddress(
                "Ichalkaranji, Maharashtra"
        );
    }

    // =========================================
    // 1. EMPTY CART
    // =========================================

    @Test
    void shouldRejectCheckoutWhenCartIsEmpty() {

        when(userRepository.findByEmail(
                "customer@example.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(cartRepository.findByUserId(4L))
                .thenReturn(
                        Optional.of(cart)
                );

        when(cartItemRepository.findByCartId(2L))
                .thenReturn(
                        List.of()
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                orderService.checkout(
                                        "customer@example.com",
                                        checkoutRequest
                                )
                );

        assertEquals(
                "Cart is empty",
                exception.getMessage()
        );

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }

    // =========================================
    // 2. UNAVAILABLE VEHICLE
    // =========================================

    @Test
    void shouldRejectUnavailableVehicle() {

        vehicle.setAvailable(false);

        prepareCart();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                orderService.checkout(
                                        "customer@example.com",
                                        checkoutRequest
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("is unavailable")
        );

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }

    // =========================================
    // 3. INSUFFICIENT STOCK
    // =========================================

    @Test
    void shouldRejectWhenQuantityExceedsStock() {

        vehicle.setStock(1);

        cartItem.setQuantity(3);

        prepareCart();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                orderService.checkout(
                                        "customer@example.com",
                                        checkoutRequest
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Insufficient stock")
        );

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }

    // =========================================
    // 4. SUCCESSFUL CHECKOUT
    // =========================================

    @Test
    void shouldCreateOrderSuccessfully() {

        prepareCart();

        when(orderRepository.save(
                any(Order.class)
        )).thenAnswer(invocation -> {

            Order order =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    order,
                    "id",
                    10L
            );

            return order;
        });

        when(orderItemRepository.findByOrderId(10L))
                .thenReturn(List.of());

        OrderResponseDTO response =
                orderService.checkout(
                        "customer@example.com",
                        checkoutRequest
                );

        assertNotNull(response);

        assertEquals(
                10L,
                response.getOrderId()
        );

        assertEquals(
                "PLACED",
                response.getStatus()
        );

        assertEquals(
                "PENDING",
                response.getPaymentStatus()
        );

        assertEquals(
                14800000.0,
                response.getSubtotal()
        );

        assertEquals(
                14800000.0,
                response.getTotalAmount()
        );

        assertEquals(
                2,
                vehicle.getStock()
        );

        verify(
                orderRepository,
                times(1)
        ).save(any(Order.class));

        verify(
                orderItemRepository,
                times(1)
        ).save(any(OrderItem.class));

        verify(
                vehicleRepository,
                times(1)
        ).save(vehicle);

        verify(
                cartItemRepository,
                times(1)
        ).deleteAll(any());

        verify(
                notificationService,
                times(1)
        ).createNotification(
                eq(user),
                eq("Order Created"),
                anyString(),
                eq("ORDER_CREATED")
        );
    }

    // =========================================
    // 5. STOCK BECOMES ZERO
    // =========================================

    @Test
    void shouldMakeVehicleUnavailableWhenStockBecomesZero() {

        vehicle.setStock(1);
        cartItem.setQuantity(1);

        prepareCart();

        when(orderRepository.save(
                any(Order.class)
        )).thenAnswer(invocation -> {

            Order order =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    order,
                    "id",
                    11L
            );

            return order;
        });

        when(orderItemRepository.findByOrderId(11L))
                .thenReturn(List.of());

        orderService.checkout(
                "customer@example.com",
                checkoutRequest
        );

        assertEquals(
                0,
                vehicle.getStock()
        );

        assertFalse(
                vehicle.getAvailable()
        );

        verify(vehicleRepository)
                .save(vehicle);
    }

    // =========================================
    // 6. CANCEL ORDER + RESTORE STOCK
    // =========================================

    @Test
    void shouldCancelOrderAndRestoreStock() {

        Order order =
                new Order();

        ReflectionTestUtils.setField(
                order,
                "id",
                20L
        );

        order.setUser(user);
        order.setStatus("PLACED");
        order.setPaymentStatus("PENDING");
        order.setSubtotal(14800000.0);
        order.setDiscountAmount(0.0);
        order.setTotalAmount(14800000.0);

        order.setShippingAddress(
                "Ichalkaranji, Maharashtra"
        );

        vehicle.setStock(2);
        vehicle.setAvailable(true);

        OrderItem orderItem =
                new OrderItem();

        ReflectionTestUtils.setField(
                orderItem,
                "id",
                10L
        );

        orderItem.setOrder(order);
        orderItem.setVehicle(vehicle);
        orderItem.setQuantity(1);
        orderItem.setPrice(14800000.0);

        when(userRepository.findByEmail(
                "customer@example.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(orderRepository.findById(20L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository.findByOrderId(20L))
                .thenReturn(
                        List.of(orderItem)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDTO response =
                orderService.cancelOrder(
                        "customer@example.com",
                        20L
                );

        assertEquals(
                "CANCELLED",
                response.getStatus()
        );

        assertEquals(
                3,
                vehicle.getStock()
        );

        assertTrue(
                vehicle.getAvailable()
        );

        verify(vehicleRepository)
                .save(vehicle);

        verify(orderRepository)
                .save(order);
    }

    // =========================================
    // COMMON MOCK SETUP
    // =========================================

    private void prepareCart() {

        when(userRepository.findByEmail(
                "customer@example.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(cartRepository.findByUserId(4L))
                .thenReturn(
                        Optional.of(cart)
                );

        when(cartItemRepository.findByCartId(2L))
                .thenReturn(
                        List.of(cartItem)
                );
    }
}