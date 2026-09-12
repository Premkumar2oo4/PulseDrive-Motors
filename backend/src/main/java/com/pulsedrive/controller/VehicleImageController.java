package com.pulsedrive.controller;

import com.pulsedrive.dto.VehicleImageRequestDTO;
import com.pulsedrive.dto.VehicleImageResponseDTO;

import com.pulsedrive.service.VehicleImageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-images")
public class VehicleImageController {

    private final VehicleImageService
            vehicleImageService;

    public VehicleImageController(
            VehicleImageService vehicleImageService) {

        this.vehicleImageService =
                vehicleImageService;
    }


    // =====================================================
    // OLD URL-BASED IMAGE CREATION
    // =====================================================

    @PostMapping
    public ResponseEntity<VehicleImageResponseDTO>
    createImage(
            @Valid
            @RequestBody
            VehicleImageRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        vehicleImageService
                                .createImage(dto)
                );
    }


    // =====================================================
    // CLOUDINARY MULTIPART UPLOAD
    // =====================================================

    @PostMapping(
            value = "/upload/{vehicleId}",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<VehicleImageResponseDTO>
    uploadImage(
            @PathVariable Long vehicleId,

            @RequestParam(
                    value = "imageType",
                    defaultValue = "EXTERIOR"
            )
            String imageType,

            @RequestParam("file")
            MultipartFile file) {

        VehicleImageResponseDTO response =
                vehicleImageService
                        .uploadImage(
                                vehicleId,
                                imageType,
                                file
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // GET IMAGES BY VEHICLE
    // =====================================================

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<
            List<VehicleImageResponseDTO>>
    getImagesByVehicle(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                vehicleImageService
                        .findByVehicleId(
                                vehicleId
                        )
        );
    }


    // =====================================================
    // DELETE IMAGE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteImage(
            @PathVariable Long id) {

        vehicleImageService
                .deleteImage(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}