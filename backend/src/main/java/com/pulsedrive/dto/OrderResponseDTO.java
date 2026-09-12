package com.pulsedrive.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private Long orderId;
    private Double totalAmount;
    private String status;
    private String paymentStatus;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> items;
    private Double subtotal;
    private Double discountAmount;
    private String couponCode;

    public OrderResponseDTO() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }
    public Double getSubtotal() {
    return subtotal;
}

public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
}

public Double getDiscountAmount() {
    return discountAmount;
}

public void setDiscountAmount(Double discountAmount) {
    this.discountAmount = discountAmount;
}

public String getCouponCode() {
    return couponCode;
}

public void setCouponCode(String couponCode) {
    this.couponCode = couponCode;
}
}