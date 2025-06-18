package com.tutoringplatform.services;

import com.tutoringplatform.notification.INotificationRepository;
import com.tutoringplatform.notification.Notification;
import com.tutoringplatform.notification.NotificationType;
import com.tutoringplatform.notification.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private INotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void createNotification_Basic_Success() {
        // Arrange
        String userId = "user123";
        NotificationType type = NotificationType.BOOKING_CREATED;
        String title = "Booking Created";
        String message = "Your booking has been created";

        // Act
        Notification result = notificationService.createNotification(userId, type, title, message);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(type, result.getType());
        assertEquals(title, result.getTitle());
        assertEquals(message, result.getMessage());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_WithActionUrl_Success() {
        // Arrange
        String userId = "user123";
        NotificationType type = NotificationType.BOOKING_CONFIRMED;
        String title = "Booking Confirmed";
        String message = "Your booking has been confirmed";
        String actionUrl = "/bookings/123";
        String relatedEntityId = "booking123";

        // Act
        Notification result = notificationService.createNotification(
                userId, type, title, message, actionUrl, relatedEntityId);

        // Assert
        assertNotNull(result);
        assertEquals(actionUrl, result.getActionUrl());
        assertEquals(relatedEntityId, result.getRelatedEntityId());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getUnreadCount_ReturnsMaxNine() {
        // Arrange
        String userId = "user123";
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(15);

        // Act
        int result = notificationService.getUnreadCount(userId);

        // Assert
        assertEquals(9, result);
    }

    @Test
    void markAsRead_Success() {
        // Arrange
        String notificationId = "notif123";

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        verify(notificationRepository).markAsRead(notificationId);
    }

    @Test
    void notifyBookingCreated_CreatesTwoNotifications() {
        // Arrange
        String studentId = "student123";
        String tutorId = "tutor456";
        String bookingId = "booking789";

        // Act
        notificationService.notifyBookingCreated(studentId, tutorId, bookingId);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void cleanupOldNotifications_Success() {
        // Arrange
        int daysToKeep = 30;

        // Act
        notificationService.cleanupOldNotifications(daysToKeep);

        // Assert
        verify(notificationRepository).deleteOldNotifications(any(LocalDateTime.class));
    }
}