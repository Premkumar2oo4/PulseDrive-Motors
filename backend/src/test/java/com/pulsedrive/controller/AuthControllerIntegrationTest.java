package com.pulsedrive.controller;

import com.pulsedrive.security.JwtAuthenticationFilter;
import com.pulsedrive.security.JwtService;

import com.pulsedrive.repository.UserRepository;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.pulsedrive.service.AuthService;
import com.pulsedrive.service.PasswordResetService;
import com.pulsedrive.service.RefreshTokenService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @WebMvcTest(AuthController.class)
    @AutoConfigureMockMvc(addFilters = false)
public class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    // ==========================================
    // TEST 1 - INVALID REGISTRATION
    // ==========================================

    @Test
    void invalidRegistrationShouldReturn400()
            throws Exception {

        String json = """
                {
                    "firstName": "",
                    "lastName": "",
                    "email": "wrong-email",
                    "password": "123",
                    "phone": "123",
                    "address": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }


    // ==========================================
    // TEST 2 - INVALID LOGIN
    // ==========================================

    @Test
    void invalidLoginRequestShouldReturn400()
            throws Exception {

        String json = """
                {
                    "email": "wrong-email",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }


    // ==========================================
    // TEST 3 - LOGIN ENDPOINT IS PUBLIC
    // ==========================================

    @Test
    void loginEndpointShouldBePublic()
            throws Exception {

        String json = """
                {
                    "email": "customer@example.com",
                    "password": "Customer@123"
                }
                """;

        when(
                authService.login(any())
        ).thenReturn(null);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isOk()
                );
    }
}