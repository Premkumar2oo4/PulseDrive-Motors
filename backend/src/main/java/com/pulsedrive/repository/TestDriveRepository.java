package com.pulsedrive.repository;

import com.pulsedrive.entity.TestDrive;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TestDriveRepository
        extends JpaRepository<TestDrive, Long> {

    List<TestDrive> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    boolean existsByDealershipIdAndVehicleIdAndDateAndTime(
            Long dealershipId,
            Long vehicleId,
            LocalDate date,
            LocalTime time
    );

    List<TestDrive> findByStatusIgnoreCase(
            String status
    );
    long countByStatusIgnoreCase(String status);
}