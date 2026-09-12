package com.pulsedrive.controller;

import com.pulsedrive.dto.AdminDashboardDTO;
import com.pulsedrive.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminService adminService;

    public AdminDashboardController(
            AdminService adminService) {

        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardDTO>
    getDashboard() {

        return ResponseEntity.ok(
                adminService.getDashboard()
        );
    }
}