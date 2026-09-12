package com.pulsedrive.service;

import com.pulsedrive.dto.NotificationResponseDTO;
import com.pulsedrive.entity.Notification;
import com.pulsedrive.entity.User;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.repository.NotificationRepository;
import com.pulsedrive.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;
    }

    public Notification createNotification(
            User user,
            String title,
            String message,
            String type) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReadStatus(false);

        return notificationRepository
                .save(notification);
    }

    public List<NotificationResponseDTO>
    getMyNotifications(String email) {

        User user = getUser(email);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public long getUnreadCount(
            String email) {

        User user = getUser(email);

        return notificationRepository
                .countByUserIdAndReadStatusFalse(
                        user.getId()
                );
    }

    public NotificationResponseDTO markAsRead(
            String email,
            Long notificationId) {

        User user = getUser(email);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        if (!notification.getUser()
                .getId()
                .equals(user.getId())) {

            throw new IllegalArgumentException(
                    "You are not allowed to access this notification"
            );
        }

        notification.setReadStatus(true);

        return toDTO(
                notificationRepository.save(
                        notification
                )
        );
    }

    public void markAllAsRead(
            String email) {

        User user = getUser(email);

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        );

        notifications.forEach(notification ->
                notification.setReadStatus(true)
        );

        notificationRepository
                .saveAll(notifications);
    }

    private User getUser(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private NotificationResponseDTO toDTO(
            Notification notification) {

        NotificationResponseDTO dto =
                new NotificationResponseDTO();

        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(
                notification.getReadStatus()
        );
        dto.setCreatedAt(
                notification.getCreatedAt()
        );

        return dto;
    }
}