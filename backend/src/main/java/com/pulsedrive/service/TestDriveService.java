package com.pulsedrive.service;

import com.pulsedrive.dto.TestDriveRequestDTO;
import com.pulsedrive.dto.TestDriveResponseDTO;

import com.pulsedrive.entity.Dealership;
import com.pulsedrive.entity.TestDrive;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;

import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.DealershipRepository;
import com.pulsedrive.repository.TestDriveRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TestDriveService {

    private final TestDriveRepository testDriveRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final DealershipRepository dealershipRepository;

    public TestDriveService(
            TestDriveRepository testDriveRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            DealershipRepository dealershipRepository) {

        this.testDriveRepository = testDriveRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.dealershipRepository = dealershipRepository;
    }

    public TestDriveResponseDTO bookTestDrive(
            String email,
            TestDriveRequestDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Vehicle vehicle = vehicleRepository
                .findById(dto.getVehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: "
                                        + dto.getVehicleId()
                        )
                );

        Dealership dealership =
                dealershipRepository
                        .findById(dto.getDealershipId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dealership not found with id: "
                                                + dto.getDealershipId()
                                )
                        );

        if (dto.getDate() == null ||
                dto.getTime() == null) {

            throw new IllegalArgumentException(
                    "Test drive date and time are required"
            );
        }

        if (dto.getDate().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Test drive date cannot be in the past"
            );
        }

        boolean slotExists =
                testDriveRepository
                        .existsByDealershipIdAndVehicleIdAndDateAndTime(
                                dealership.getId(),
                                vehicle.getId(),
                                dto.getDate(),
                                dto.getTime()
                        );

        if (slotExists) {

            throw new DuplicateResourceException(
                    "This test drive slot is already booked"
            );
        }

        TestDrive testDrive = new TestDrive();

        testDrive.setUser(user);
        testDrive.setVehicle(vehicle);
        testDrive.setDealership(dealership);
        testDrive.setDate(dto.getDate());
        testDrive.setTime(dto.getTime());
        testDrive.setNotes(dto.getNotes());
        testDrive.setStatus("PENDING");

        TestDrive saved =
                testDriveRepository.save(testDrive);

        return toDTO(saved);
    }

    public List<TestDriveResponseDTO> getMyTestDrives(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return testDriveRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TestDriveResponseDTO getMyTestDriveById(
            String email,
            Long id) {

        TestDrive testDrive =
                getTestDrive(id);

        if (!testDrive.getUser()
                .getEmail()
                .equals(email)) {

            throw new IllegalArgumentException(
                    "You are not allowed to access this test drive"
            );
        }

        return toDTO(testDrive);
    }

    public void cancelTestDrive(
            String email,
            Long id) {

        TestDrive testDrive =
                getTestDrive(id);

        if (!testDrive.getUser()
                .getEmail()
                .equals(email)) {

            throw new IllegalArgumentException(
                    "You are not allowed to cancel this test drive"
            );
        }

        if ("COMPLETED".equalsIgnoreCase(
                testDrive.getStatus())) {

            throw new IllegalArgumentException(
                    "Completed test drive cannot be cancelled"
            );
        }

        testDrive.setStatus("CANCELLED");

        testDriveRepository.save(testDrive);
    }

    private TestDrive getTestDrive(Long id) {

        return testDriveRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test drive not found with id: "
                                        + id
                        )
                );
    }

    private TestDriveResponseDTO toDTO(
            TestDrive testDrive) {

        TestDriveResponseDTO dto =
                new TestDriveResponseDTO();

        dto.setId(testDrive.getId());

        dto.setVehicleId(
                testDrive.getVehicle().getId()
        );

        dto.setVehicleName(
                testDrive.getVehicle().getBrand()
                        + " "
                        + testDrive.getVehicle().getModel()
        );

        dto.setDealershipId(
                testDrive.getDealership().getId()
        );

        dto.setDealershipName(
                testDrive.getDealership().getName()
        );

        dto.setDate(testDrive.getDate());
        dto.setTime(testDrive.getTime());
        dto.setStatus(testDrive.getStatus());
        dto.setNotes(testDrive.getNotes());
        dto.setCreatedAt(testDrive.getCreatedAt());

        return dto;
    }
    public List<TestDriveResponseDTO> getAllTestDrives() {

    return testDriveRepository
            .findAll()
            .stream()
            .map(this::toDTO)
            .toList();
}


public List<TestDriveResponseDTO> getTestDrivesByStatus(
        String status) {

    return testDriveRepository
            .findByStatusIgnoreCase(status)
            .stream()
            .map(this::toDTO)
            .toList();
}


public TestDriveResponseDTO updateStatus(
        Long id,
        String status) {

    TestDrive testDrive = getTestDrive(id);

    String normalizedStatus =
            status.toUpperCase();

    List<String> allowedStatuses =
            List.of(
                    "PENDING",
                    "APPROVED",
                    "REJECTED",
                    "COMPLETED",
                    "CANCELLED"
            );

    if (!allowedStatuses.contains(normalizedStatus)) {

        throw new IllegalArgumentException(
                "Invalid test drive status: " + status
        );
    }

    testDrive.setStatus(normalizedStatus);

    TestDrive updated =
            testDriveRepository.save(testDrive);

    return toDTO(updated);
}
}