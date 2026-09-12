package com.pulsedrive.dto;

public class SalesTrendDTO {

    private String period;
    private Double revenue;
    private Long orders;

    public SalesTrendDTO(String period, Double revenue, Long orders) {
        this.period = period;
        this.revenue = revenue;
        this.orders = orders;
    }

    public String getPeriod() {
        return period;
    }

    public Double getRevenue() {
        return revenue;
    }

    public Long getOrders() {
        return orders;
    }
}
