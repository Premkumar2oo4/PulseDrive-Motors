package com.pulsedrive.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class PaymentRequestDTO {

    @NotNull(message = "Order id is required")
private Long orderId;

@NotBlank(message = "Payment method is required")
private String paymentMethod;

    public PaymentRequestDTO() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}