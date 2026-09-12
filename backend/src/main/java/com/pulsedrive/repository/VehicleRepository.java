package com.pulsedrive.repository;

import com.pulsedrive.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VehicleRepository
                extends JpaRepository<Vehicle, Long>,
                JpaSpecificationExecutor<Vehicle> {

        List<Vehicle> findByBrand(String brand);

        List<Vehicle> findByFuelType(String fuelType);

        List<Vehicle> findByBodyType(String bodyType);

        List<Vehicle> findByAvailableTrue();

        List<Vehicle> findByBrandIgnoreCase(String brand);

        List<Vehicle> findByFuelTypeIgnoreCase(String fuelType);

        List<Vehicle> findByBodyTypeIgnoreCase(String bodyType);

        List<Vehicle> findByTransmissionIgnoreCase(String transmission);

        List<Vehicle> findByPriceBetween(
                        Double minPrice,
                        Double maxPrice);

        long countByAvailableTrue();

        List<Vehicle> findByStockLessThanEqual(Integer stock);

        List<Vehicle> findByAvailableFalse();

        long countByStockLessThanEqual(Integer stock);
}