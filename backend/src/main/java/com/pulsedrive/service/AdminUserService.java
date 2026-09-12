package com.pulsedrive.service;

import com.pulsedrive.dto.UserResponseDTO;
import com.pulsedrive.entity.User;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // GET ALL CUSTOMERS
    public List<UserResponseDTO> getAllCustomers() {

        return userRepository
                .findByRoleIgnoreCase("CUSTOMER")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET USER BY ID
    public UserResponseDTO getUserById(
            Long userId) {

        User user = getUser(userId);

        return toDTO(user);
    }

    // SEARCH USERS
    public List<UserResponseDTO> searchUsers(
            String keyword) {

        if (keyword == null ||
                keyword.isBlank()) {

            return userRepository
                    .findAll()
                    .stream()
                    .map(this::toDTO)
                    .toList();
        }

        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // DISABLE ACCOUNT
    public UserResponseDTO disableUser(
            Long userId) {

        User user = getUser(userId);

        if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {

            throw new IllegalArgumentException(
                    "Admin account cannot be disabled from customer management"
            );
        }

        user.setEnabled(false);

        return toDTO(
                userRepository.save(user)
        );
    }

    // ENABLE ACCOUNT
    public UserResponseDTO enableUser(
            Long userId) {

        User user = getUser(userId);

        user.setEnabled(true);

        return toDTO(
                userRepository.save(user)
        );
    }

    private User getUser(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    private UserResponseDTO toDTO(
            User user) {

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