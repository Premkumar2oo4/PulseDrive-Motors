package com.pulsedrive.dto;

public class InventorySummaryDTO {

    private Long totalVehicles;
    private Long availableVehicles;
    private Long unavailableVehicles;
    private Long lowStockVehicles;

    public InventorySummaryDTO() {
    }

    public Long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(Long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public Long getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(Long availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public Long getUnavailableVehicles() {
        return unavailableVehicles;
    }

    public void setUnavailableVehicles(Long unavailableVehicles) {
        this.unavailableVehicles = unavailableVehicles;
    }

    public Long getLowStockVehicles() {
        return lowStockVehicles;
    }

    public void setLowStockVehicles(Long lowStockVehicles) {
        this.lowStockVehicles = lowStockVehicles;
    }
}