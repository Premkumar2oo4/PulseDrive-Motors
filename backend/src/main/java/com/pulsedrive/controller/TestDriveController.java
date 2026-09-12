package com.pulsedrive.controller;

import com.pulsedrive.dto.TestDriveRequestDTO;
import com.pulsedrive.dto.TestDriveResponseDTO;
import com.pulsedrive.service.TestDriveService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(
            TestDriveService testDriveService) {

        this.testDriveService =
                testDriveService;
    }

    @PostMapping
    public ResponseEntity<TestDriveResponseDTO>
    bookTestDrive(
            Principal principal,
            @Valid @RequestBody TestDriveRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        testDriveService.bookTestDrive(
                                principal.getName(),
                                dto
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<TestDriveResponseDTO>>
    getMyTestDrives(
            Principal principal) {

        return ResponseEntity.ok(
                testDriveService
                        .getMyTestDrives(
                                principal.getName()
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDriveResponseDTO>
    getMyTestDriveById(
            Principal principal,
            @PathVariable Long id) {

        return ResponseEntity.ok(
                testDriveService
                        .getMyTestDriveById(
                                principal.getName(),
                                id
                        )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    cancelTestDrive(
            Principal principal,
            @PathVariable Long id) {

        testDriveService
                .cancelTestDrive(
                        principal.getName(),
                        id
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}