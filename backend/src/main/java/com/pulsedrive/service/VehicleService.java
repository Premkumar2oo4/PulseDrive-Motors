package com.pulsedrive.service;

import com.pulsedrive.dto.VehicleRequestDTO;
import com.pulsedrive.dto.VehicleResponseDTO;
import com.pulsedrive.entity.Category;
import com.pulsedrive.entity.Vehicle;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.mapper.VehicleMapper;
import com.pulsedrive.repository.CategoryRepository;
import com.pulsedrive.repository.VehicleRepository;
import com.pulsedrive.specification.VehicleSpecification;
import com.pulsedrive.dto.VehiclePageResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(
            VehicleRepository vehicleRepository,
            CategoryRepository categoryRepository,
            VehicleMapper vehicleMapper) {

        this.vehicleRepository = vehicleRepository;
        this.categoryRepository = categoryRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public List<VehicleResponseDTO> searchVehicles(
            String brand,
            String fuelType,
            String bodyType,
            String transmission,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDirection) {

        Specification<Vehicle> specification =
                Specification.unrestricted();

        if (brand != null && !brand.isBlank()) {
            specification = specification.and(
                    VehicleSpecification.hasBrand(brand)
            );
        }

        if (fuelType != null && !fuelType.isBlank()) {
            specification = specification.and(
                    VehicleSpecification.hasFuelType(fuelType)
            );
        }

        if (bodyType != null && !bodyType.isBlank()) {
            specification = specification.and(
                    VehicleSpecification.hasBodyType(bodyType)
            );
        }

        if (transmission != null && !transmission.isBlank()) {
            specification = specification.and(
                    VehicleSpecification.hasTransmission(transmission)
            );
        }

        if (minPrice != null) {
            specification = specification.and(
                    VehicleSpecification
                            .priceGreaterThanOrEqualTo(minPrice)
            );
        }

        if (maxPrice != null) {
            specification = specification.and(
                    VehicleSpecification
                            .priceLessThanOrEqualTo(maxPrice)
            );
        }

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank()) {

            Sort.Direction direction =
                    "desc".equalsIgnoreCase(sortDirection)
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            sort = Sort.by(direction, sortBy);
        }

        return vehicleRepository
                .findAll(specification, sort)
                .stream()
                .map(vehicleMapper::toResponseDTO)
                .toList();
    }


    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::toResponseDTO)
                .toList();
    }

    public VehicleResponseDTO getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + id
                        )
                );

        return vehicleMapper.toResponseDTO(vehicle);
    }


    public VehicleResponseDTO createVehicle(
            VehicleRequestDTO dto) {

        Category category = null;

        if (dto.getCategoryId() != null) {

            category = categoryRepository
                    .findById(dto.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found with id: "
                                            + dto.getCategoryId()
                            )
                    );
        }

        Vehicle vehicle =
                vehicleMapper.toEntity(dto, category);

        Vehicle savedVehicle =
                vehicleRepository.save(vehicle);

        return vehicleMapper.toResponseDTO(savedVehicle);
    }


    public VehicleResponseDTO updateVehicle(
            Long id,
            VehicleRequestDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + id
                        )
                );

        Category category = null;

        if (dto.getCategoryId() != null) {

            category = categoryRepository
                    .findById(dto.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found with id: "
                                            + dto.getCategoryId()
                            )
                    );
        }

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

        Vehicle updatedVehicle =
                vehicleRepository.save(vehicle);

        return vehicleMapper.toResponseDTO(updatedVehicle);
    }


    public VehiclePageResponseDTO searchVehiclesPaginated(
        String brand,
        String fuelType,
        String bodyType,
        String transmission,
        Double minPrice,
        Double maxPrice,
        String sortBy,
        String sortDirection,
        int page,
        int size) {

    Specification<Vehicle> specification =
            Specification.unrestricted();

    // Brand filter
    if (brand != null && !brand.isBlank()) {
        specification = specification.and(
                VehicleSpecification.hasBrand(brand)
        );
    }

    // Fuel type filter
    if (fuelType != null && !fuelType.isBlank()) {
        specification = specification.and(
                VehicleSpecification.hasFuelType(fuelType)
        );
    }

    // Body type filter
    if (bodyType != null && !bodyType.isBlank()) {
        specification = specification.and(
                VehicleSpecification.hasBodyType(bodyType)
        );
    }

    // Transmission filter
    if (transmission != null && !transmission.isBlank()) {
        specification = specification.and(
                VehicleSpecification.hasTransmission(transmission)
        );
    }

    // Minimum price
    if (minPrice != null) {
        specification = specification.and(
                VehicleSpecification
                        .priceGreaterThanOrEqualTo(minPrice)
        );
    }

    // Maximum price
    if (maxPrice != null) {
        specification = specification.and(
                VehicleSpecification
                        .priceLessThanOrEqualTo(maxPrice)
        );
    }

    // Sorting
    Sort sort = Sort.unsorted();

    if (sortBy != null && !sortBy.isBlank()) {

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        sort = Sort.by(direction, sortBy);
    }

    // Pagination
    PageRequest pageRequest =
            PageRequest.of(page, size, sort);

    Page<Vehicle> vehiclePage =
            vehicleRepository.findAll(
                    specification,
                    pageRequest
            );

    List<VehicleResponseDTO> vehicles =
            vehiclePage.getContent()
                    .stream()
                    .map(vehicleMapper::toResponseDTO)
                    .toList();

    return new VehiclePageResponseDTO(
            vehicles,
            vehiclePage.getNumber(),
            vehiclePage.getSize(),
            vehiclePage.getTotalElements(),
            vehiclePage.getTotalPages(),
            vehiclePage.isFirst(),
            vehiclePage.isLast()
    );
}
    public void deleteVehicle(Long id) {

        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Vehicle not found with id: " + id
            );
        }

        vehicleRepository.deleteById(id);
    }
}