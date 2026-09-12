package com.pulsedrive.controller;

import com.pulsedrive.dto.VehiclePageResponseDTO;
import com.pulsedrive.dto.VehicleRequestDTO;
import com.pulsedrive.dto.VehicleResponseDTO;
import com.pulsedrive.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {

        return ResponseEntity.ok(
                vehicleService.getAllVehicles()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                vehicleService.getVehicleById(id)
        );
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(
           @Valid @RequestBody VehicleRequestDTO dto) {

        return ResponseEntity.ok(
                vehicleService.createVehicle(dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(
            @PathVariable Long id) {

        vehicleService.deleteVehicle(id);

        return ResponseEntity.ok(
                "Vehicle deleted successfully"
        );
    }

    @PutMapping("/{id}")
public ResponseEntity<VehicleResponseDTO> updateVehicle(
        @PathVariable Long id,
        @Valid @RequestBody VehicleRequestDTO dto) {

    return ResponseEntity.ok(
            vehicleService.updateVehicle(id, dto)
    );
}
@GetMapping("/search")
public ResponseEntity<List<VehicleResponseDTO>> searchVehicles(

        @RequestParam(required = false)
        String brand,

        @RequestParam(required = false)
        String fuelType,

        @RequestParam(required = false)
        String bodyType,

        @RequestParam(required = false)
        String transmission,

        @RequestParam(required = false)
        Double minPrice,

        @RequestParam(required = false)
        Double maxPrice,

        @RequestParam(required = false)
        String sortBy,

        @RequestParam(required = false)
        String sortDirection) {

    return ResponseEntity.ok(
            vehicleService.searchVehicles(
                    brand,
                    fuelType,
                    bodyType,
                    transmission,
                    minPrice,
                    maxPrice,
                    sortBy,
                    sortDirection
            )
    );
}
@GetMapping("/search/page")
public ResponseEntity<VehiclePageResponseDTO> searchVehiclesPaginated(

        @RequestParam(required = false)
        String brand,

        @RequestParam(required = false)
        String fuelType,

        @RequestParam(required = false)
        String bodyType,

        @RequestParam(required = false)
        String transmission,

        @RequestParam(required = false)
        Double minPrice,

        @RequestParam(required = false)
        Double maxPrice,

        @RequestParam(required = false, defaultValue = "price")
        String sortBy,

        @RequestParam(required = false, defaultValue = "asc")
        String sortDirection,

        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "10")
        int size) {

    return ResponseEntity.ok(
            vehicleService.searchVehiclesPaginated(
                    brand,
                    fuelType,
                    bodyType,
                    transmission,
                    minPrice,
                    maxPrice,
                    sortBy,
                    sortDirection,
                    page,
                    size
            )
    );
}
}