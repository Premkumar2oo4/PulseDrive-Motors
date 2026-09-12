package com.pulsedrive.service;

import com.pulsedrive.dto.ChangePasswordRequestDTO;
import com.pulsedrive.dto.UserResponseDTO;
import com.pulsedrive.dto.UserUpdateRequestDTO;

import com.pulsedrive.entity.User;

import com.pulsedrive.exception.InvalidCredentialsException;
import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // GET CURRENT LOGGED-IN USER
    // ==========================================
    public UserResponseDTO getCurrentUser(String email) {

        User user = getUserByEmail(email);

        return toResponseDTO(user);
    }


    // ==========================================
    // UPDATE CURRENT USER PROFILE
    // ==========================================
    public UserResponseDTO updateCurrentUser(
            String email,
            UserUpdateRequestDTO dto) {

        User user = getUserByEmail(email);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        User updatedUser =
                userRepository.save(user);

        return toResponseDTO(updatedUser);
    }


    // ==========================================
    // CHANGE PASSWORD
    // ==========================================
    public void changePassword(
            String email,
            ChangePasswordRequestDTO dto) {

        User user = getUserByEmail(email);

        boolean passwordMatches =
                passwordEncoder.matches(
                        dto.getCurrentPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new InvalidCredentialsException(
                    "Current password is incorrect"
            );
        }

        // Prevent setting same password again
        if (passwordEncoder.matches(
                dto.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        dto.getNewPassword()
                );

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }


    // ==========================================
    // FIND USER BY EMAIL
    // ==========================================
    private User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }


    // ==========================================
    // ENTITY -> RESPONSE DTO
    // ==========================================
    private UserResponseDTO toResponseDTO(User user) {

        UserResponseDTO dto =
                new UserResponseDTO();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        return dto;
    }
}