package com.pulsedrive.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class CheckoutRequestDTO {

    @NotBlank(message = "Shipping address is required")
@Size(
        min = 5,
        max = 1000,
        message = "Shipping address must be between 5 and 1000 characters"
)
private String shippingAddress;

@Size(
        max = 100,
        message = "Coupon code cannot exceed 100 characters"
)
private String couponCode;

    public CheckoutRequestDTO() {
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}