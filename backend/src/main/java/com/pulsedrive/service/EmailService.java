package com.pulsedrive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String toEmail,
            String token) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);

        message.setSubject(
                "PulseDrive Motors - Password Reset"
        );

        message.setText(
                "You requested a password reset.\n\n"
                        + "Your reset token is:\n"
                        + token
                        + "\n\n"
                        + "This token expires in 15 minutes.\n\n"
                        + "If you did not request this reset, ignore this email."
        );

        mailSender.send(message);
    }
}