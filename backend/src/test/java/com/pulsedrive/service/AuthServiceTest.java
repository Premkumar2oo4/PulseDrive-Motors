package com.pulsedrive.service;

import com.pulsedrive.dto.AuthResponseDTO;
import com.pulsedrive.dto.LoginRequestDTO;
import com.pulsedrive.dto.RefreshTokenResponseDTO;
import com.pulsedrive.dto.RegisterRequestDTO;

import com.pulsedrive.entity.RefreshToken;
import com.pulsedrive.entity.User;

import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.InvalidCredentialsException;

import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        ReflectionTestUtils.setField(
                user,
                "id",
                4L
        );

        user.setFirstName("Test");
        user.setLastName("Customer");
        user.setEmail("customer@example.com");
        user.setPassword("encoded-password");
        user.setPhone("9876543210");
        user.setAddress("Maharashtra");
        user.setRole("CUSTOMER");
        user.setEnabled(true);
    }

    // ==========================================
    // TEST 1 - REGISTER SUCCESS
    // ==========================================

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequestDTO dto =
                new RegisterRequestDTO();

        dto.setFirstName("Test");
        dto.setLastName("Customer");
        dto.setEmail("customer@example.com");
        dto.setPassword("Customer@123");
        dto.setPhone("9876543210");
        dto.setAddress("Maharashtra");

        when(
                userRepository.existsByEmail(
                        "customer@example.com"
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode(
                        "Customer@123"
                )
        ).thenReturn(
                "encoded-password"
        );

        when(
                userRepository.save(
                        any(User.class)
                )
        ).thenAnswer(invocation -> {

            User saved =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    saved,
                    "id",
                    10L
            );

            return saved;
        });

        AuthResponseDTO response =
                authService.register(dto);

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId()
        );

        assertEquals(
                "customer@example.com",
                response.getEmail()
        );

        assertEquals(
                "CUSTOMER",
                response.getRole()
        );

        assertEquals(
                "Registration successful",
                response.getMessage()
        );

        verify(
                passwordEncoder,
                times(1)
        ).encode("Customer@123");

        verify(
                userRepository,
                times(1)
        ).save(any(User.class));
    }

    // ==========================================
    // TEST 2 - DUPLICATE EMAIL
    // ==========================================

    @Test
    void shouldRejectDuplicateEmailRegistration() {

        RegisterRequestDTO dto =
                new RegisterRequestDTO();

        dto.setEmail(
                "customer@example.com"
        );

        when(
                userRepository.existsByEmail(
                        "customer@example.com"
                )
        ).thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () ->
                                authService.register(dto)
                );

        assertEquals(
                "User already exists with email: customer@example.com",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    // ==========================================
    // TEST 3 - LOGIN SUCCESS
    // ==========================================

    @Test
    void shouldLoginSuccessfully() {

        LoginRequestDTO dto =
                new LoginRequestDTO();

        dto.setEmail(
                "customer@example.com"
        );

        dto.setPassword(
                "Customer@123"
        );

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "Customer@123",
                        "encoded-password"
                )
        ).thenReturn(true);

        when(
                jwtService.generateToken(
                        "customer@example.com",
                        "CUSTOMER"
                )
        ).thenReturn(
                "access-token"
        );

        RefreshToken refreshToken =
                createRefreshToken();

        when(
                refreshTokenService
                        .createRefreshToken(
                                "customer@example.com"
                        )
        ).thenReturn(
                refreshToken
        );

        AuthResponseDTO response =
                authService.login(dto);

        assertNotNull(response);

        assertEquals(
                4L,
                response.getId()
        );

        assertEquals(
                "Login successful",
                response.getMessage()
        );

        assertEquals(
                "access-token",
                response.getToken()
        );

        assertEquals(
                "refresh-token-123",
                response.getRefreshToken()
        );
    }

    // ==========================================
    // TEST 4 - WRONG PASSWORD
    // ==========================================

    @Test
    void shouldRejectWrongPassword() {

        LoginRequestDTO dto =
                new LoginRequestDTO();

        dto.setEmail(
                "customer@example.com"
        );

        dto.setPassword(
                "WrongPassword"
        );

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "WrongPassword",
                        "encoded-password"
                )
        ).thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () ->
                                authService.login(dto)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(
                jwtService,
                never()
        ).generateToken(
                anyString(),
                anyString()
        );

        verify(
                refreshTokenService,
                never()
        ).createRefreshToken(
                anyString()
        );
    }

    // ==========================================
    // TEST 5 - USER NOT FOUND
    // ==========================================

    @Test
    void shouldRejectUnknownEmail() {

        LoginRequestDTO dto =
                new LoginRequestDTO();

        dto.setEmail(
                "unknown@example.com"
        );

        dto.setPassword(
                "Customer@123"
        );

        when(
                userRepository.findByEmail(
                        "unknown@example.com"
                )
        ).thenReturn(
                Optional.empty()
        );

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () ->
                                authService.login(dto)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 6 - DISABLED USER
    // ==========================================

    @Test
    void shouldRejectDisabledUserLogin() {

        user.setEnabled(false);

        LoginRequestDTO dto =
                new LoginRequestDTO();

        dto.setEmail(
                "customer@example.com"
        );

        dto.setPassword(
                "Customer@123"
        );

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () ->
                                authService.login(dto)
                );

        assertEquals(
                "Account is disabled",
                exception.getMessage()
        );

        verify(
                passwordEncoder,
                never()
        ).matches(
                anyString(),
                anyString()
        );
    }

    // ==========================================
    // TEST 7 - REFRESH ACCESS TOKEN
    // ==========================================

    @Test
    void shouldRefreshAccessTokenSuccessfully() {

        RefreshToken refreshToken =
                createRefreshToken();

        when(
                refreshTokenService
                        .validateRefreshToken(
                                "refresh-token-123"
                        )
        ).thenReturn(
                refreshToken
        );

        when(
                jwtService.generateToken(
                        "customer@example.com",
                        "CUSTOMER"
                )
        ).thenReturn(
                "new-access-token"
        );

        RefreshTokenResponseDTO response =
                authService
                        .refreshAccessToken(
                                "refresh-token-123"
                        );

        assertNotNull(response);

        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token-123",
                response.getRefreshToken()
        );
    }

    // ==========================================
    // HELPER
    // ==========================================

    private RefreshToken createRefreshToken() {

        RefreshToken refreshToken =
                new RefreshToken();

        ReflectionTestUtils.setField(
                refreshToken,
                "id",
                1L
        );

        refreshToken.setUser(user);

        refreshToken.setToken(
                "refresh-token-123"
        );

        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(7)
        );

        refreshToken.setRevoked(false);

        return refreshToken;
    }
}