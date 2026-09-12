package com.pulsedrive.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ==========================================
    // 1. PUBLIC AUTH ENDPOINT
    // ==========================================

    @Test
    void loginEndpointShouldBePublic()
            throws Exception {

        String json = """
                {
                    "email": "wrong-email",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    // ==========================================
    // 2. PUBLIC VEHICLE ENDPOINT
    // ==========================================

    @Test
    void vehicleGetShouldBePublic()
            throws Exception {

        mockMvc.perform(
                        get("/api/vehicles")
                )
                .andExpect(
                        status().isOk()
                );
    }

    // ==========================================
    // 3. CART REQUIRES AUTHENTICATION
    // ==========================================

    @Test
    void cartWithoutJwtShouldBeRejected()
            throws Exception {

        mockMvc.perform(
                        get("/api/cart")
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    // ==========================================
    // 4. ADMIN DASHBOARD REQUIRES ADMIN
    // ==========================================

    @Test
    void adminDashboardWithoutJwtShouldBeRejected()
            throws Exception {

        mockMvc.perform(
                        get("/api/admin/dashboard")
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    // ==========================================
    // 5. FORGOT PASSWORD IS PUBLIC
    // ==========================================

    @Test
    void forgotPasswordShouldReachValidationWithoutJwt()
            throws Exception {

        String json = """
                {
                    "email": "invalid"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/forgot-password")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }
}