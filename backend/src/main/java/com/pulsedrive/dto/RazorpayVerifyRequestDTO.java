package com.pulsedrive.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class RazorpayVerifyRequestDTO {

    @NotNull(message = "Order id is required")
private Long orderId;

@NotBlank(message = "Razorpay payment id is required")
private String razorpayPaymentId;

@NotBlank(message = "Razorpay order id is required")
private String razorpayOrderId;

@NotBlank(message = "Razorpay signature is required")
private String razorpaySignature;

    public RazorpayVerifyRequestDTO() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}