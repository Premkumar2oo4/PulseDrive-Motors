package com.pulsedrive.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status;

    private String paymentMethod;

    private String transactionId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private String razorpayOrderId;

    private String razorpayPaymentId;

    @Column(length = 1000)
    private String razorpaySignature;

    @Column(name = "refund_status")
private String refundStatus;

@Column(name = "refund_id")
private String refundId;

@Column(name = "refunded_amount")
private Double refundedAmount;

@Column(name = "refunded_at")
private LocalDateTime refundedAt;

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
    public String getRefundStatus() {
    return refundStatus;
}

public void setRefundStatus(String refundStatus) {
    this.refundStatus = refundStatus;
}

public String getRefundId() {
    return refundId;
}

public void setRefundId(String refundId) {
    this.refundId = refundId;
}

public Double getRefundedAmount() {
    return refundedAmount;
}

public void setRefundedAmount(Double refundedAmount) {
    this.refundedAmount = refundedAmount;
}

public LocalDateTime getRefundedAt() {
    return refundedAt;
}

public void setRefundedAt(LocalDateTime refundedAt) {
    this.refundedAt = refundedAt;
}

}