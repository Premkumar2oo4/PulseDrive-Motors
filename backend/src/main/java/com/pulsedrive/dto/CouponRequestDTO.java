package com.pulsedrive.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CouponRequestDTO {

    @NotBlank(message = "Coupon code is required")
private String code;

@NotBlank(message = "Discount type is required")
private String discountType;

@NotNull(message = "Discount value is required")
@Positive(message = "Discount value must be greater than 0")
private Double discountValue;

@DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Minimum amount cannot be negative"
)
private Double minimumAmount;

@NotNull(message = "Expiry date is required")
private LocalDate expiryDate;

@NotNull(message = "Active status is required")
private Boolean active;;

    public CouponRequestDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Double getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}