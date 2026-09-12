package com.pulsedrive.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class ReviewRequestDTO {

    @NotNull(message = "Vehicle id is required")
private Long vehicleId;

@NotNull(message = "Rating is required")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating cannot exceed 5")
private Integer rating;

@Size(max = 2000, message = "Comment cannot exceed 2000 characters")
private String comment;

    public ReviewRequestDTO() {
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}