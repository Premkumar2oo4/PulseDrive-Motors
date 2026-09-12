package com.pulsedrive.mapper;

import com.pulsedrive.dto.VehicleFeatureRequestDTO;
import com.pulsedrive.dto.VehicleFeatureResponseDTO;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.VehicleFeature;
import org.springframework.stereotype.Component;

@Component
public class VehicleFeatureMapper {

    public VehicleFeature toEntity(
            VehicleFeatureRequestDTO dto,
            Vehicle vehicle) {

        VehicleFeature feature = new VehicleFeature();

        feature.setFeatureName(dto.getFeatureName());
        feature.setVehicle(vehicle);

        return feature;
    }

    public VehicleFeatureResponseDTO toResponseDTO(
            VehicleFeature feature) {

        VehicleFeatureResponseDTO dto =
                new VehicleFeatureResponseDTO();

        dto.setId(feature.getId());
        dto.setFeatureName(feature.getFeatureName());

        if (feature.getVehicle() != null) {
            dto.setVehicleId(
                    feature.getVehicle().getId()
            );
        }

        return dto;
    }
}