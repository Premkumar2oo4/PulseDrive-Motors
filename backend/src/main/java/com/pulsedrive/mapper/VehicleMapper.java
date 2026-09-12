package com.pulsedrive.mapper;

import com.pulsedrive.dto.VehicleRequestDTO;
import com.pulsedrive.dto.VehicleResponseDTO;
import com.pulsedrive.entity.Category;
import com.pulsedrive.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequestDTO dto, Category category) {

        Vehicle vehicle = new Vehicle();

        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setVariant(dto.getVariant());
        vehicle.setYear(dto.getYear());
        vehicle.setPrice(dto.getPrice());
        vehicle.setDiscount(dto.getDiscount());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setEngine(dto.getEngine());
        vehicle.setHorsepower(dto.getHorsepower());
        vehicle.setTorque(dto.getTorque());
        vehicle.setMileage(dto.getMileage());
        vehicle.setBodyType(dto.getBodyType());
        vehicle.setColor(dto.getColor());
        vehicle.setSeatingCapacity(dto.getSeatingCapacity());
        vehicle.setDriveType(dto.getDriveType());
        vehicle.setSafetyRating(dto.getSafetyRating());
        vehicle.setDescription(dto.getDescription());
        vehicle.setStock(dto.getStock());
        vehicle.setAvailable(dto.getAvailable());

        vehicle.setCategory(category);

        return vehicle;
    }

    public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {

        VehicleResponseDTO dto = new VehicleResponseDTO();

        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setVariant(vehicle.getVariant());
        dto.setYear(vehicle.getYear());
        dto.setPrice(vehicle.getPrice());
        dto.setDiscount(vehicle.getDiscount());
        dto.setFuelType(vehicle.getFuelType());
        dto.setTransmission(vehicle.getTransmission());
        dto.setEngine(vehicle.getEngine());
        dto.setHorsepower(vehicle.getHorsepower());
        dto.setTorque(vehicle.getTorque());
        dto.setMileage(vehicle.getMileage());
        dto.setBodyType(vehicle.getBodyType());
        dto.setColor(vehicle.getColor());
        dto.setSeatingCapacity(vehicle.getSeatingCapacity());
        dto.setDriveType(vehicle.getDriveType());
        dto.setSafetyRating(vehicle.getSafetyRating());
        dto.setDescription(vehicle.getDescription());
        dto.setStock(vehicle.getStock());
        dto.setAvailable(vehicle.getAvailable());

        if (vehicle.getCategory() != null) {
            dto.setCategoryId(vehicle.getCategory().getId());
            dto.setCategoryName(vehicle.getCategory().getName());
        }

        return dto;
    }
}