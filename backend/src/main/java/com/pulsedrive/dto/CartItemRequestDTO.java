package com.pulsedrive.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class CartItemRequestDTO {

    @NotNull(message = "Vehicle id is required")
private Long vehicleId;

@NotNull(message = "Quantity is required")
@Min(value = 1, message = "Quantity must be at least 1")
private Integer quantity;

    public CartItemRequestDTO() {
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}