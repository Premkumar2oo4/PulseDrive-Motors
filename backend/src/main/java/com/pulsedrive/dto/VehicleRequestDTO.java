package com.pulsedrive.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
public class VehicleRequestDTO {

    @NotBlank(message = "Brand is required")
private String brand;

@NotBlank(message = "Model is required")
private String model;

private String variant;

@NotNull(message = "Year is required")
@Min(value = 1900, message = "Year must be valid")
private Integer year;

@NotNull(message = "Price is required")
@PositiveOrZero(message = "Price cannot be negative")
private Double price;

@PositiveOrZero(message = "Discount cannot be negative")
private Double discount;

@NotBlank(message = "Fuel type is required")
private String fuelType;

@NotBlank(message = "Transmission is required")
private String transmission;

private String engine;

@PositiveOrZero(message = "Horsepower cannot be negative")
private Integer horsepower;

@PositiveOrZero(message = "Torque cannot be negative")
private Integer torque;

@PositiveOrZero(message = "Mileage cannot be negative")
private Double mileage;

@NotBlank(message = "Body type is required")
private String bodyType;

private String color;

@PositiveOrZero(message = "Seating capacity cannot be negative")
private Integer seatingCapacity;

private String driveType;

@PositiveOrZero(message = "Safety rating cannot be negative")
private Double safetyRating;

@Size(max = 2000, message = "Description cannot exceed 2000 characters")
private String description;

@NotNull(message = "Stock is required")
@PositiveOrZero(message = "Stock cannot be negative")
private Integer stock;

@NotNull(message = "Availability is required")
private Boolean available;

private Long categoryId;

    // No-argument constructor
    public VehicleRequestDTO() {
    }

    // Getters and Setters

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public Integer getTorque() {
        return torque;
    }

    public void setTorque(Integer torque) {
        this.torque = torque;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public Double getSafetyRating() {
        return safetyRating;
    }

    public void setSafetyRating(Double safetyRating) {
        this.safetyRating = safetyRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}