package com.pulsedrive.service;

import com.pulsedrive.dto.CheckoutRequestDTO;
import com.pulsedrive.dto.OrderItemResponseDTO;
import com.pulsedrive.dto.OrderResponseDTO;

import com.pulsedrive.entity.Cart;
import com.pulsedrive.entity.CartItem;
import com.pulsedrive.entity.Order;
import com.pulsedrive.entity.OrderItem;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.Coupon;
import com.pulsedrive.entity.CouponUsage;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.CartItemRepository;
import com.pulsedrive.repository.CartRepository;
import com.pulsedrive.repository.OrderItemRepository;
import com.pulsedrive.repository.OrderRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;
import com.pulsedrive.repository.CouponUsageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserRepository userRepository;
        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final VehicleRepository vehicleRepository;
        private final CouponService couponService;
        private final CouponUsageRepository couponUsageRepository;
private final NotificationService notificationService;
        public OrderService(
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        CartRepository cartRepository,
                        CouponService couponService,
                        CartItemRepository cartItemRepository,
                        CouponUsageRepository couponUsageRepository,
                        VehicleRepository vehicleRepository,NotificationService notificationService) {

                this.orderRepository = orderRepository;
                this.orderItemRepository = orderItemRepository;
                this.userRepository = userRepository;
                this.cartRepository = cartRepository;
                this.cartItemRepository = cartItemRepository;
                this.vehicleRepository = vehicleRepository;
                this.couponService = couponService;
                this.couponUsageRepository = couponUsageRepository;
                this.notificationService = notificationService;
        }

        @Transactional
        public OrderResponseDTO checkout(
                        String email,
                        CheckoutRequestDTO dto) {

                // ==============================
                // 1. FIND USER
                // ==============================

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                // ==============================
                // 2. FIND CART
                // ==============================

                Cart cart = cartRepository
                                .findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found"));

                List<CartItem> cartItems = cartItemRepository
                                .findByCartId(cart.getId());

                if (cartItems.isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Cart is empty");
                }

                // ==============================
                // 3. CALCULATE SUBTOTAL
                // ==============================

                double subtotal = 0.0;

                for (CartItem item : cartItems) {

                        Vehicle vehicle = item.getVehicle();

                        if (!Boolean.TRUE.equals(
                                        vehicle.getAvailable())) {

                                throw new IllegalArgumentException(
                                                vehicle.getBrand()
                                                                + " "
                                                                + vehicle.getModel()
                                                                + " is unavailable");
                        }

                        if (vehicle.getStock() == null ||
                                        item.getQuantity() > vehicle.getStock()) {

                                throw new IllegalArgumentException(
                                                "Insufficient stock for "
                                                                + vehicle.getBrand()
                                                                + " "
                                                                + vehicle.getModel());
                        }

                        subtotal += item.getPrice()
                                        * item.getQuantity();
                }

                // ==============================
                // 4. COUPON VALIDATION
                // ==============================

                double discountAmount = 0.0;

                String appliedCouponCode = null;

                Coupon appliedCoupon = null;

                if (dto.getCouponCode() != null &&
                                !dto.getCouponCode().isBlank()) {

                        // Find and validate coupon
                        appliedCoupon = couponService.getValidCoupon(
                                        dto.getCouponCode(),
                                        subtotal);

                        // Check if user already used coupon
                        boolean alreadyUsed = couponUsageRepository
                                        .existsByCouponIdAndUserId(
                                                        appliedCoupon.getId(),
                                                        user.getId());

                        if (alreadyUsed) {

                                throw new IllegalArgumentException(
                                                "You have already used this coupon");
                        }

                        // Calculate discount
                        discountAmount = couponService.calculateDiscount(
                                        dto.getCouponCode(),
                                        subtotal);

                        appliedCouponCode = appliedCoupon.getCode();
                }

                // ==============================
                // 5. FINAL TOTAL
                // ==============================

                double totalAmount = subtotal - discountAmount;

                // ==============================
                // 6. CREATE ORDER
                // ==============================

                Order order = new Order();

                order.setUser(user);

                order.setSubtotal(subtotal);

                order.setDiscountAmount(
                                discountAmount);

                order.setCouponCode(
                                appliedCouponCode);

                order.setTotalAmount(
                                totalAmount);

                order.setStatus("PLACED");

                order.setPaymentStatus(
                                "PENDING");

                order.setShippingAddress(
                                dto.getShippingAddress());

                Order savedOrder = orderRepository.save(order);

                notificationService.createNotification(
        user,
        "Order Created",
        "Your order #" + savedOrder.getId()
                + " has been placed successfully.",
        "ORDER_CREATED"
);

                // ==============================
                // 7. CREATE ORDER ITEMS
                // ==============================

                for (CartItem cartItem : cartItems) {

                        Vehicle vehicle = cartItem.getVehicle();

                        OrderItem orderItem = new OrderItem();

                        orderItem.setOrder(
                                        savedOrder);

                        orderItem.setVehicle(
                                        vehicle);

                        orderItem.setQuantity(
                                        cartItem.getQuantity());

                        orderItem.setPrice(
                                        cartItem.getPrice());

                        orderItemRepository.save(
                                        orderItem);

                        // ==============================
                        // 8. REDUCE STOCK
                        // ==============================

                        vehicle.setStock(
                                        vehicle.getStock()
                                                        - cartItem.getQuantity());

                        if (vehicle.getStock() <= 0) {

                                vehicle.setAvailable(false);
                        }

                        vehicleRepository.save(
                                        vehicle);
                }

                // ==============================
                // 9. CLEAR CART
                // ==============================

                cartItemRepository.deleteAll(
                                cartItems);

                // ==============================
                // 10. SAVE COUPON USAGE
                // ==============================

                if (appliedCoupon != null) {

                        CouponUsage couponUsage = new CouponUsage();

                        couponUsage.setCoupon(
                                        appliedCoupon);

                        couponUsage.setUser(
                                        user);

                        couponUsage.setOrder(
                                        savedOrder);

                        couponUsageRepository.save(
                                        couponUsage);
                }

                // ==============================
                // 11. RETURN ORDER
                // ==============================

                return buildOrderResponse(
                                savedOrder);
        }

        // ==================================
        // GET MY ORDERS
        // ==================================

        public List<OrderResponseDTO> getMyOrders(
                        String email) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                return orderRepository
                                .findByUserIdOrderByCreatedAtDesc(
                                                user.getId())
                                .stream()
                                .map(this::buildOrderResponse)
                                .toList();
        }

        // ==================================
        // GET ORDER BY ID
        // ==================================

        public OrderResponseDTO getOrderById(
                        String email,
                        Long orderId) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                Order order = orderRepository
                                .findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: "
                                                                + orderId));

                if (!order.getUser()
                                .getId()
                                .equals(user.getId())) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to access this order");
                }

                return buildOrderResponse(
                                order);
        }

        // ==================================
        // BUILD ORDER RESPONSE
        // ==================================

        private OrderResponseDTO buildOrderResponse(
                        Order order) {

                List<OrderItemResponseDTO> itemDTOs = orderItemRepository
                                .findByOrderId(
                                                order.getId())
                                .stream()
                                .map(this::toItemDTO)
                                .toList();

                OrderResponseDTO dto = new OrderResponseDTO();

                dto.setOrderId(
                                order.getId());

                dto.setSubtotal(
                                order.getSubtotal());

                dto.setDiscountAmount(
                                order.getDiscountAmount());

                dto.setCouponCode(
                                order.getCouponCode());

                dto.setTotalAmount(
                                order.getTotalAmount());

                dto.setStatus(
                                order.getStatus());

                dto.setPaymentStatus(
                                order.getPaymentStatus());

                dto.setShippingAddress(
                                order.getShippingAddress());

                dto.setCreatedAt(
                                order.getCreatedAt());

                dto.setItems(
                                itemDTOs);

                return dto;
        }

        // ==================================
        // ORDER ITEM DTO
        // ==================================

        private OrderItemResponseDTO toItemDTO(
                        OrderItem item) {

                Vehicle vehicle = item.getVehicle();

                OrderItemResponseDTO dto = new OrderItemResponseDTO();

                dto.setVehicleId(
                                vehicle.getId());

                dto.setBrand(
                                vehicle.getBrand());

                dto.setModel(
                                vehicle.getModel());

                dto.setVariant(
                                vehicle.getVariant());

                dto.setQuantity(
                                item.getQuantity());

                dto.setPrice(
                                item.getPrice());

                dto.setSubtotal(
                                item.getPrice()
                                                * item.getQuantity());

                return dto;
        }

        @Transactional
        public OrderResponseDTO cancelOrder(
                        String email,
                        Long orderId) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                Order order = orderRepository
                                .findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: " + orderId));

                // Make sure this order belongs to the logged-in customer
                if (!order.getUser()
                                .getId()
                                .equals(user.getId())) {

                        throw new IllegalArgumentException(
                                        "You are not allowed to cancel this order");
                }

                String status = order.getStatus();

                if ("CANCELLED".equalsIgnoreCase(status)) {
                        throw new IllegalArgumentException(
                                        "Order is already cancelled");
                }

                if ("DELIVERED".equalsIgnoreCase(status)) {
                        throw new IllegalArgumentException(
                                        "Delivered order cannot be cancelled");
                }

                if ("READY_FOR_DELIVERY".equalsIgnoreCase(status)) {
                        throw new IllegalArgumentException(
                                        "Order cannot be cancelled after it is ready for delivery");
                }

                // Restore stock because checkout reduced it
                List<OrderItem> orderItems = orderItemRepository
                                .findByOrderId(order.getId());

                for (OrderItem orderItem : orderItems) {

                        Vehicle vehicle = orderItem.getVehicle();

                        vehicle.setStock(
                                        vehicle.getStock()
                                                        + orderItem.getQuantity());

                        vehicle.setAvailable(true);

                        vehicleRepository.save(vehicle);
                }

                order.setStatus("CANCELLED");

                Order updatedOrder = orderRepository.save(order);

                return buildOrderResponse(updatedOrder);
        }

        public List<OrderResponseDTO> getAllOrders() {

                return orderRepository
                                .findAll()
                                .stream()
                                .map(this::buildOrderResponse)
                                .toList();
        }

        @Transactional
        public OrderResponseDTO updateOrderStatus(
                        Long orderId,
                        String status) {

                Order order = orderRepository
                                .findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: " + orderId));

                String normalizedStatus = status.trim().toUpperCase();

                List<String> allowedStatuses = List.of(
                                "PLACED",
                                "CONFIRMED",
                                "PROCESSING",
                                "READY_FOR_DELIVERY",
                                "DELIVERED",
                                "CANCELLED");

                if (!allowedStatuses.contains(normalizedStatus)) {

                        throw new IllegalArgumentException(
                                        "Invalid order status: " + status);
                }

                if ("DELIVERED".equalsIgnoreCase(
                                order.getStatus())) {

                        throw new IllegalArgumentException(
                                        "Delivered order status cannot be changed");
                }

                order.setStatus(normalizedStatus);

                Order updatedOrder = orderRepository.save(order);

                return buildOrderResponse(updatedOrder);
        }
}