package com.pulsedrive.controller;

import com.pulsedrive.dto.InventorySummaryDTO;
import com.pulsedrive.dto.VehicleResponseDTO;
import com.pulsedrive.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final AdminService adminService;

    public AdminInventoryController(
            AdminService adminService) {

        this.adminService = adminService;
    }

    @GetMapping("/summary")
    public ResponseEntity<InventorySummaryDTO>
    getInventorySummary() {

        return ResponseEntity.ok(
                adminService.getInventorySummary()
        );
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<VehicleResponseDTO>>
    getLowStockVehicles(
            @RequestParam(required = false)
            Integer threshold) {

        return ResponseEntity.ok(
                adminService
                        .getLowStockVehicles(threshold)
        );
    }

    @GetMapping("/unavailable")
    public ResponseEntity<List<VehicleResponseDTO>>
    getUnavailableVehicles() {

        return ResponseEntity.ok(
                adminService
                        .getUnavailableVehicles()
        );
    }
}