package com.pulsedrive.dto;

public class VehicleImageResponseDTO {

    private Long id;
    private String imageUrl;
    private String imageType;
    private Long vehicleId;
    private String cloudinaryPublicId;
    public VehicleImageResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String getCloudinaryPublicId() {
    return cloudinaryPublicId;
}

public void setCloudinaryPublicId(
        String cloudinaryPublicId) {

    this.cloudinaryPublicId =
            cloudinaryPublicId;
}
}