package com.pulsedrive.controller;

import com.pulsedrive.dto.UserResponseDTO;
import com.pulsedrive.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {

        this.adminUserService =
                adminUserService;
    }

    // GET ALL CUSTOMERS
    @GetMapping("/customers")
    public ResponseEntity<List<UserResponseDTO>>
    getAllCustomers() {

        return ResponseEntity.ok(
                adminUserService.getAllCustomers()
        );
    }

    // GET SINGLE USER
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO>
    getUserById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>>
    searchUsers(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                adminUserService.searchUsers(keyword)
        );
    }

    // DISABLE ACCOUNT
    @PutMapping("/{userId}/disable")
    public ResponseEntity<UserResponseDTO>
    disableUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.disableUser(userId)
        );
    }

    // ENABLE ACCOUNT
    @PutMapping("/{userId}/enable")
    public ResponseEntity<UserResponseDTO>
    enableUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.enableUser(userId)
        );
    }
}