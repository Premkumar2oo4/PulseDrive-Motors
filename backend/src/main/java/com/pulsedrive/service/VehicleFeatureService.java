package com.pulsedrive.service;

import com.pulsedrive.dto.VehicleFeatureRequestDTO;
import com.pulsedrive.dto.VehicleFeatureResponseDTO;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.VehicleFeature;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.mapper.VehicleFeatureMapper;
import com.pulsedrive.repository.VehicleFeatureRepository;
import com.pulsedrive.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleFeatureService {

    private final VehicleFeatureRepository vehicleFeatureRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleFeatureMapper vehicleFeatureMapper;

    public VehicleFeatureService(
            VehicleFeatureRepository vehicleFeatureRepository,
            VehicleRepository vehicleRepository,
            VehicleFeatureMapper vehicleFeatureMapper) {

        this.vehicleFeatureRepository = vehicleFeatureRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleFeatureMapper = vehicleFeatureMapper;
    }

    public VehicleFeatureResponseDTO createFeature(
            VehicleFeatureRequestDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(
                dto.getVehicleId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Vehicle not found with id: "
                                + dto.getVehicleId()
                )
        );

        VehicleFeature feature =
                vehicleFeatureMapper.toEntity(dto, vehicle);

        VehicleFeature savedFeature =
                vehicleFeatureRepository.save(feature);

        return vehicleFeatureMapper.toResponseDTO(savedFeature);
    }

    public List<VehicleFeatureResponseDTO> getFeaturesByVehicleId(
            Long vehicleId) {

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException(
                    "Vehicle not found with id: " + vehicleId
            );
        }

        return vehicleFeatureRepository
                .findByVehicleId(vehicleId)
                .stream()
                .map(vehicleFeatureMapper::toResponseDTO)
                .toList();
    }

    public void deleteFeature(Long id) {

        if (!vehicleFeatureRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Vehicle feature not found with id: " + id
            );
        }

        vehicleFeatureRepository.deleteById(id);
    }
}