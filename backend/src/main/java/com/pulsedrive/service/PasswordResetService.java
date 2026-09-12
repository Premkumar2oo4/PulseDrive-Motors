package com.pulsedrive.service;

import com.pulsedrive.entity.PasswordResetToken;
import com.pulsedrive.entity.User;

import com.pulsedrive.exception.ResourceNotFoundException;

import com.pulsedrive.repository.PasswordResetTokenRepository;
import com.pulsedrive.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

        private final UserRepository userRepository;

        private final PasswordResetTokenRepository passwordResetTokenRepository;

        private final PasswordEncoder passwordEncoder;
        private final EmailService emailService;

        public PasswordResetService(
                        UserRepository userRepository,
                        PasswordResetTokenRepository passwordResetTokenRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService) {

                this.userRepository = userRepository;
                this.passwordResetTokenRepository = passwordResetTokenRepository;
                this.passwordEncoder = passwordEncoder;
                this.emailService = emailService;
        }

        @Transactional
public String createResetToken(String email) {

    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found"
                    )
            );

    PasswordResetToken resetToken =
            passwordResetTokenRepository
                    .findByUserId(user.getId())
                    .orElse(new PasswordResetToken());

    // Reuse existing row instead of delete + insert
    resetToken.setUser(user);

    resetToken.setToken(
            UUID.randomUUID().toString()
    );

    resetToken.setExpiresAt(
            LocalDateTime.now()
                    .plusMinutes(15)
    );

    resetToken.setUsed(false);

    PasswordResetToken saved =
            passwordResetTokenRepository
                    .save(resetToken);

    emailService.sendPasswordResetEmail(
            user.getEmail(),
            saved.getToken()
    );

    return saved.getToken();
}

        @Transactional
        public void resetPassword(
                        String token,
                        String newPassword) {

                PasswordResetToken resetToken = passwordResetTokenRepository
                                .findByToken(token)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid reset token"));

                if (Boolean.TRUE.equals(
                                resetToken.getUsed())) {

                        throw new IllegalArgumentException(
                                        "Reset token has already been used");
                }

                if (resetToken.getExpiresAt()
                                .isBefore(LocalDateTime.now())) {

                        throw new IllegalArgumentException(
                                        "Reset token has expired");
                }

                User user = resetToken.getUser();

                user.setPassword(
                                passwordEncoder.encode(
                                                newPassword));

                userRepository.save(user);

                resetToken.setUsed(true);

                passwordResetTokenRepository
                                .save(resetToken);
        }
}