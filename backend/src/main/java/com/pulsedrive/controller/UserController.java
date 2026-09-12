package com.pulsedrive.controller;

import com.pulsedrive.dto.ChangePasswordRequestDTO;
import com.pulsedrive.dto.UserResponseDTO;
import com.pulsedrive.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pulsedrive.dto.UserUpdateRequestDTO;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            Principal principal) {

        return ResponseEntity.ok(
                userService.getCurrentUser(
                        principal.getName()
                )
        );
    }
    @PutMapping("/me")
public ResponseEntity<UserResponseDTO> updateCurrentUser(
        Principal principal,
        @RequestBody UserUpdateRequestDTO dto) {

    return ResponseEntity.ok(
            userService.updateCurrentUser(
                    principal.getName(),
                    dto
            )
    );
}
@PutMapping("/me/password")
public ResponseEntity<String> changePassword(
        Principal principal,
        @RequestBody ChangePasswordRequestDTO dto) {

    userService.changePassword(
            principal.getName(),
            dto
    );

    return ResponseEntity.ok(
            "Password changed successfully"
    );
}
}