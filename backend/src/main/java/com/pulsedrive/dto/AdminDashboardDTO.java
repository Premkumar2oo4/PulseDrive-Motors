package com.pulsedrive.dto;

import java.util.List;

public class AdminDashboardDTO {

    private Long totalCustomers;
    private Long totalVehicles;
    private Long availableVehicles;

    private Long totalOrders;
    private Long pendingOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;

    private Long totalTestDrives;
    private Long pendingTestDrives;

    private Double totalRevenue;
    private List<SalesTrendDTO> monthlySales;
    private List<SalesTrendDTO> yearlySales;

    public AdminDashboardDTO() {
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
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

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(Long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public Long getTotalTestDrives() {
        return totalTestDrives;
    }

    public void setTotalTestDrives(Long totalTestDrives) {
        this.totalTestDrives = totalTestDrives;
    }

    public Long getPendingTestDrives() {
        return pendingTestDrives;
    }

    public void setPendingTestDrives(Long pendingTestDrives) {
        this.pendingTestDrives = pendingTestDrives;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<SalesTrendDTO> getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(List<SalesTrendDTO> monthlySales) {
        this.monthlySales = monthlySales;
    }

    public List<SalesTrendDTO> getYearlySales() {
        return yearlySales;
    }

    public void setYearlySales(List<SalesTrendDTO> yearlySales) {
        this.yearlySales = yearlySales;
    }
}