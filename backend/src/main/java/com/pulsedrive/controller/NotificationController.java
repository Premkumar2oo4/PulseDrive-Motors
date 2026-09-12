package com.pulsedrive.controller;

import com.pulsedrive.dto.NotificationResponseDTO;
import com.pulsedrive.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>>
    getMyNotifications(
            Principal principal) {

        return ResponseEntity.ok(
                notificationService
                        .getMyNotifications(
                                principal.getName()
                        )
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>>
    getUnreadCount(
            Principal principal) {

        long count =
                notificationService
                        .getUnreadCount(
                                principal.getName()
                        );

        return ResponseEntity.ok(
                Map.of(
                        "unreadCount",
                        count
                )
        );
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO>
    markAsRead(
            Principal principal,
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
                notificationService
                        .markAsRead(
                                principal.getName(),
                                notificationId
                        )
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<String>
    markAllAsRead(
            Principal principal) {

        notificationService
                .markAllAsRead(
                        principal.getName()
                );

        return ResponseEntity.ok(
                "All notifications marked as read"
        );
    }
}