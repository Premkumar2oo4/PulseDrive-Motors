package com.pulsedrive.dto;

public class VehicleFeatureRequestDTO {

    private String featureName;
    private Long vehicleId;

    public VehicleFeatureRequestDTO() {
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}