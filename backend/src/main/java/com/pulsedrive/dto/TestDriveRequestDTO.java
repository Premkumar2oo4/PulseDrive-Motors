package com.pulsedrive.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class TestDriveRequestDTO {

    @NotNull(message = "Vehicle id is required")
private Long vehicleId;

@NotNull(message = "Dealership id is required")
private Long dealershipId;

@NotNull(message = "Test drive date is required")
@FutureOrPresent(message = "Test drive date cannot be in the past")
private LocalDate date;

@NotNull(message = "Test drive time is required")
private LocalTime time;

@Size(max = 1000, message = "Notes cannot exceed 1000 characters")
private String notes;

    public TestDriveRequestDTO() {
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(Long dealershipId) {
        this.dealershipId = dealershipId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}