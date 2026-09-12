package com.pulsedrive.service;

import com.pulsedrive.entity.RefreshToken;
import com.pulsedrive.entity.User;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.RefreshTokenRepository;
import com.pulsedrive.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {

        this.refreshTokenRepository =
                refreshTokenRepository;

        this.userRepository =
                userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalArgumentException(
                    "Account is disabled"
            );
        }

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(
                UUID.randomUUID().toString()
                        + UUID.randomUUID()
        );

        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(7)
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository
                .save(refreshToken);
    }

    public RefreshToken validateRefreshToken(
            String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid refresh token"
                                )
                        );

        if (Boolean.TRUE.equals(
                refreshToken.getRevoked())) {

            throw new IllegalArgumentException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        if (!Boolean.TRUE.equals(
                refreshToken.getUser().getEnabled())) {

            throw new IllegalArgumentException(
                    "Account is disabled"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(
            String token) {

        RefreshToken refreshToken =
                validateRefreshToken(token);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(
                refreshToken
        );
    }

    @Transactional
    public void revokeAllUserTokens(
            Long userId) {

        refreshTokenRepository
                .findByUserId(userId)
                .forEach(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}