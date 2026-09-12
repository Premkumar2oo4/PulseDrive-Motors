package com.pulsedrive.service;

import com.pulsedrive.dto.VehicleImageRequestDTO;
import com.pulsedrive.dto.VehicleImageResponseDTO;

import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.entity.VehicleImage;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.mapper.VehicleImageMapper;

import com.pulsedrive.repository.VehicleImageRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class VehicleImageService {

    private final VehicleImageRepository vehicleImageRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleImageMapper vehicleImageMapper;
    private final CloudinaryService cloudinaryService;

    public VehicleImageService(
            VehicleImageRepository vehicleImageRepository,
            VehicleRepository vehicleRepository,
            VehicleImageMapper vehicleImageMapper,
            CloudinaryService cloudinaryService) {

        this.vehicleImageRepository =
                vehicleImageRepository;

        this.vehicleRepository =
                vehicleRepository;

        this.vehicleImageMapper =
                vehicleImageMapper;

        this.cloudinaryService =
                cloudinaryService;
    }


    // =====================================================
    // EXISTING URL-BASED IMAGE CREATION
    // =====================================================

    public VehicleImageResponseDTO createImage(
            VehicleImageRequestDTO dto) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(dto.getVehicleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vehicle not found with id: "
                                                + dto.getVehicleId()
                                )
                        );

        VehicleImage image =
                new VehicleImage();

        image.setImageUrl(
                dto.getImageUrl()
        );

        image.setImageType(
                dto.getImageType()
        );

        image.setCloudinaryPublicId("");

        image.setVehicle(vehicle);

        VehicleImage saved =
                vehicleImageRepository
                        .save(image);

        return vehicleImageMapper
                .toResponseDTO(saved);
    }


    // =====================================================
    // CLOUDINARY IMAGE UPLOAD
    // =====================================================

    public VehicleImageResponseDTO uploadImage(
            Long vehicleId,
            String imageType,
            MultipartFile file) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vehicle not found with id: "
                                                + vehicleId
                                )
                        );

        try {

            Map<String, Object> uploadResult =
                    cloudinaryService
                            .uploadImage(file);

            String secureUrl =
                    uploadResult
                            .get("secure_url")
                            .toString();

            String publicId =
                    uploadResult
                            .get("public_id")
                            .toString();

            VehicleImage image =
                    new VehicleImage();

            image.setVehicle(vehicle);

            image.setImageType(
                    imageType
            );

            image.setImageUrl(
                    secureUrl
            );

            image.setCloudinaryPublicId(
                    publicId
            );

            VehicleImage saved =
                    vehicleImageRepository
                            .save(image);

            return vehicleImageMapper
                    .toResponseDTO(saved);

        } catch (IOException exception) {

            throw new IllegalArgumentException(
                    "Unable to upload vehicle image. Check Cloudinary configuration and credentials."
            );
        } catch (RuntimeException exception) {

            throw new IllegalArgumentException(
                    "Unable to upload vehicle image. Check Cloudinary configuration and credentials."
            );
        }
    }


    // =====================================================
    // GET VEHICLE IMAGES
    // =====================================================

    public List<VehicleImageResponseDTO>
    findByVehicleId(Long vehicleId) {

        if (!vehicleRepository
                .existsById(vehicleId)) {

            throw new ResourceNotFoundException(
                    "Vehicle not found with id: "
                            + vehicleId
            );
        }

        return vehicleImageRepository
                .findByVehicleId(vehicleId)
                .stream()
                .map(
                        vehicleImageMapper
                                ::toResponseDTO
                )
                .toList();
    }


    // =====================================================
    // DELETE IMAGE
    // =====================================================

    public void deleteImage(Long id) {

        VehicleImage image =
                vehicleImageRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vehicle image not found with id: "
                                                + id
                                )
                        );

        /*
         * Images created before Cloudinary integration
         * may not have a public ID.
         */
        if (image.getCloudinaryPublicId()
                != null
                &&
                !image.getCloudinaryPublicId()
                        .isBlank()) {

            try {

                cloudinaryService
                        .deleteImage(
                                image.getCloudinaryPublicId()
                        );

            } catch (IOException exception) {

                throw new IllegalArgumentException(
                        "Unable to delete image from Cloudinary"
                );
            }
        }

        vehicleImageRepository
                .delete(image);
    }
}