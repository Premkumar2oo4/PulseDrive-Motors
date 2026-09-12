package com.pulsedrive.controller;

import com.pulsedrive.dto.VehicleFeatureRequestDTO;
import com.pulsedrive.dto.VehicleFeatureResponseDTO;
import com.pulsedrive.service.VehicleFeatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-features")
public class VehicleFeatureController {

    private final VehicleFeatureService vehicleFeatureService;

    public VehicleFeatureController(
            VehicleFeatureService vehicleFeatureService) {
        this.vehicleFeatureService = vehicleFeatureService;
    }

    @PostMapping
    public ResponseEntity<VehicleFeatureResponseDTO> createFeature(
            @RequestBody VehicleFeatureRequestDTO dto) {

        VehicleFeatureResponseDTO response =
                vehicleFeatureService.createFeature(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleFeatureResponseDTO>> getFeaturesByVehicleId(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                vehicleFeatureService
                        .getFeaturesByVehicleId(vehicleId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(
            @PathVariable Long id) {

        vehicleFeatureService.deleteFeature(id);

        return ResponseEntity.noContent().build();
    }
}