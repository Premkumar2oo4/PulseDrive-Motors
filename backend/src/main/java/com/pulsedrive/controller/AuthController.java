package com.pulsedrive.controller;

import com.pulsedrive.dto.AuthResponseDTO;
import com.pulsedrive.dto.ForgotPasswordRequestDTO;
import com.pulsedrive.dto.LoginRequestDTO;
import com.pulsedrive.dto.RegisterRequestDTO;
import com.pulsedrive.dto.ResetPasswordRequestDTO;
import com.pulsedrive.dto.RefreshTokenRequestDTO;
import com.pulsedrive.dto.RefreshTokenResponseDTO;
import com.pulsedrive.service.AuthService;
import com.pulsedrive.service.PasswordResetService;
import com.pulsedrive.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
private final RefreshTokenService refreshTokenService;
    public AuthController(
        AuthService authService,
        PasswordResetService passwordResetService,
        RefreshTokenService refreshTokenService) {

    this.authService = authService;
    this.passwordResetService = passwordResetService;
    this.refreshTokenService = refreshTokenService;
}

    // ==========================================
    // REGISTER
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
        @Valid @RequestBody RegisterRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        authService.register(dto)
                );
    }

    // ==========================================
    // LOGIN
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
          @Valid  @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(
                authService.login(dto)
        );
    }

    // ==========================================
    // FORGOT PASSWORD
    // ==========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
          @Valid  @RequestBody ForgotPasswordRequestDTO dto) {

        passwordResetService.createResetToken(
        dto.getEmail()
);

return ResponseEntity.ok(
        "Password reset instructions have been sent"
);
    }

    // ==========================================
    // RESET PASSWORD
    // ==========================================
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
         @Valid   @RequestBody ResetPasswordRequestDTO dto) {

        passwordResetService.resetPassword(
                dto.getToken(),
                dto.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password reset successful"
        );
    }
    @PostMapping("/refresh")
public ResponseEntity<RefreshTokenResponseDTO>
refreshToken(
        @Valid @RequestBody RefreshTokenRequestDTO dto) {

    return ResponseEntity.ok(
            authService.refreshAccessToken(
                    dto.getRefreshToken()
            )
    );
}
@PostMapping("/logout")
public ResponseEntity<String> logout(
        @Valid @RequestBody RefreshTokenRequestDTO dto) {

    refreshTokenService.revokeToken(
            dto.getRefreshToken()
    );

    return ResponseEntity.ok(
            "Logout successful"
    );
}
}