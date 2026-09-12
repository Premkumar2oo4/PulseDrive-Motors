package com.pulsedrive.controller;

import com.pulsedrive.dto.TestDriveResponseDTO;
import com.pulsedrive.service.TestDriveService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/test-drives")
public class AdminTestDriveController {

    private final TestDriveService testDriveService;

    public AdminTestDriveController(
            TestDriveService testDriveService) {

        this.testDriveService =
                testDriveService;
    }

    // GET ALL TEST DRIVES
    @GetMapping
    public ResponseEntity<List<TestDriveResponseDTO>>
    getAllTestDrives() {

        return ResponseEntity.ok(
                testDriveService.getAllTestDrives()
        );
    }


    // FILTER BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TestDriveResponseDTO>>
    getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                testDriveService
                        .getTestDrivesByStatus(status)
        );
    }


    // UPDATE STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<TestDriveResponseDTO>
    updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                testDriveService
                        .updateStatus(
                                id,
                                status
                        )
        );
    }
}