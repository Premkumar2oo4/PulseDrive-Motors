package com.pulsedrive.mapper;

import com.pulsedrive.dto.VehicleImageRequestDTO;
import com.pulsedrive.dto.VehicleImageResponseDTO;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.VehicleImage;
import org.springframework.stereotype.Component;

@Component
public class VehicleImageMapper {

    public VehicleImage toEntity(
            VehicleImageRequestDTO dto,
            Vehicle vehicle) {

        VehicleImage image = new VehicleImage();

        image.setImageUrl(dto.getImageUrl());
        image.setImageType(dto.getImageType());
        image.setVehicle(vehicle);

        return image;
    }

    public VehicleImageResponseDTO toResponseDTO(
            VehicleImage image) {

        VehicleImageResponseDTO dto =
                new VehicleImageResponseDTO();

        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType());
        dto.setCloudinaryPublicId(
        image.getCloudinaryPublicId()
);

        if (image.getVehicle() != null) {
            dto.setVehicleId(
                    image.getVehicle().getId()
            );
        }

        return dto;
    }
}