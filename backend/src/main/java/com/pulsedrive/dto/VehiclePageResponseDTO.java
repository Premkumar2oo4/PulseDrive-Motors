package com.pulsedrive.dto;

import java.util.List;

public class VehiclePageResponseDTO {

    private List<VehicleResponseDTO> vehicles;

    private int currentPage;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    public VehiclePageResponseDTO() {
    }

    public VehiclePageResponseDTO(
            List<VehicleResponseDTO> vehicles,
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last) {

        this.vehicles = vehicles;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public List<VehicleResponseDTO> getVehicles() {
        return vehicles;
    }

    public void setVehicles(
            List<VehicleResponseDTO> vehicles) {
        this.vehicles = vehicles;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
