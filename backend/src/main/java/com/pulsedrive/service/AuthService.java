package com.pulsedrive.service;

import com.pulsedrive.dto.RegisterRequestDTO;
import com.pulsedrive.entity.RefreshToken;
import com.pulsedrive.entity.User;
import com.pulsedrive.dto.AuthResponseDTO;

import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.InvalidCredentialsException;
import com.pulsedrive.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pulsedrive.dto.LoginRequestDTO;
import com.pulsedrive.dto.RefreshTokenResponseDTO;
import com.pulsedrive.security.JwtService;
@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
private final RefreshTokenService refreshTokenService;
    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
}

    public AuthResponseDTO register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + dto.getEmail());
        }

        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        user.setPassword(
                passwordEncoder.encode(dto.getPassword()));

        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        // Every normal registration becomes CUSTOMER
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);

        AuthResponseDTO response = new AuthResponseDTO();

        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setMessage("Registration successful");

        return response;
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));
        if (!Boolean.TRUE.equals(user.getEnabled())) {

    throw new InvalidCredentialsException(
            "Account is disabled"
    );
}

        boolean passwordMatches = passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole());
                RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(
                        user.getEmail()
                );

        AuthResponseDTO response = new AuthResponseDTO();
                response.setRefreshToken(
        refreshToken.getToken()
);
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("Login successful");
        response.setToken(token);
        return response;
    }
    public RefreshTokenResponseDTO refreshAccessToken(
        String token) {

    RefreshToken refreshToken =
            refreshTokenService
                    .validateRefreshToken(token);

    User user =
            refreshToken.getUser();

    String newAccessToken =
            jwtService.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

    return new RefreshTokenResponseDTO(
            newAccessToken,
            refreshToken.getToken()
    );
}
}