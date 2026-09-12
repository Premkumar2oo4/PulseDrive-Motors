package com.pulsedrive.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "PulseDriveTestJwtSecretKey2026SecureTesting123456789"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                86400000L
        );
    }

    @Test
    void shouldGenerateValidToken() {

        String token =
                jwtService.generateToken(
                        "customer@example.com",
                        "CUSTOMER"
                );

        assertNotNull(token);

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldExtractEmail() {

        String token =
                jwtService.generateToken(
                        "customer@example.com",
                        "CUSTOMER"
                );

        String email =
                jwtService.extractEmail(token);

        assertEquals(
                "customer@example.com",
                email
        );
    }

    @Test
    void shouldExtractRole() {

        String token =
                jwtService.generateToken(
                        "admin@example.com",
                        "ADMIN"
                );

        String role =
                jwtService.extractRole(token);

        assertEquals(
                "ADMIN",
                role
        );
    }

    @Test
    void invalidTokenShouldReturnFalse() {

        boolean result =
                jwtService.isTokenValid(
                        "this-is-not-a-valid-jwt"
                );

        assertFalse(result);
    }
}