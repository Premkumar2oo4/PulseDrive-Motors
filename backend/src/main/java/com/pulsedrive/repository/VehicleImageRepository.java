package com.pulsedrive.repository;

import com.pulsedrive.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {

    List<VehicleImage> findByVehicleId(Long vehicleId);
}