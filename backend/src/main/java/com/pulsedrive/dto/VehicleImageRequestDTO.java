package com.pulsedrive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleImageRequestDTO {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotBlank(message = "Image type is required")
    private String imageType;

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;

    public VehicleImageRequestDTO() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}