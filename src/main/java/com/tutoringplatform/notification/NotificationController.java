package com.tutoringplatform.notification;

import com.tutoringplatform.shared.dto.response.NotificationListResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable String userId,
            @RequestParam(defaultValue = "20") int limit) {
        logger.debug("Getting notifications for user: {}", userId);
        List<Notification> notifications = notificationService.getRecentNotifications(userId, limit);
        NotificationListResponse response = new NotificationListResponse();
        response.setNotifications(notifications);
        response.setUnreadCount(notificationService.getUnreadCount(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        NotificationListResponse response = new NotificationListResponse();
        response.setNotifications(notifications);
        response.setUnreadCount(notificationService.getUnreadCount(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getUnreadCount(@PathVariable String userId) {
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted");
    }
}