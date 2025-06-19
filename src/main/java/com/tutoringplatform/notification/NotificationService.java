package com.tutoringplatform.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final INotificationRepository notificationRepository;

    @Autowired
    public NotificationService(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification createNotification(String userId, NotificationType type,
            String title, String message) {
        Notification notification = new Notification(userId, type, title, message);
        notificationRepository.save(notification);
        logger.info("Created notification for user {}: {}", userId, title);
        return notification;
    }

    @Transactional
    public Notification createNotification(String userId, NotificationType type,
            String title, String message,
            String actionUrl, String relatedEntityId) {
        Notification notification = new Notification(userId, type, title, message);
        notification.setActionUrl(actionUrl);
        notification.setRelatedEntityId(relatedEntityId);
        notificationRepository.save(notification);
        logger.info("Created notification with action for user {}: {}", userId, title);
        return notification;
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    public List<Notification> getRecentNotifications(String userId, int limit) {
        return notificationRepository.findRecentByUserId(userId, limit);
    }

    public int getUnreadCount(String userId) {
        int count = notificationRepository.countUnreadByUserId(userId);
        return Math.min(count, 9); // Return max 9 for display as "9+"
    }

    @Transactional
    public void markAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
        logger.debug("Marked notification {} as read", notificationId);
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadForUser(userId);
        logger.info("Marked all notifications as read for user {}", userId);
    }

    @Transactional
    public void deleteNotification(String notificationId) {
        notificationRepository.delete(notificationId);
        logger.debug("Deleted notification {}", notificationId);
    }

    @Transactional
    public void cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        notificationRepository.deleteOldNotifications(cutoffDate);
        logger.info("Cleaned up notifications older than {} days", daysToKeep);
    }

    // Convenience methods for specific notification types

    public void notifyBookingCreated(String studentId, String tutorId, String bookingId) {
        // For student
        createNotification(
                studentId,
                NotificationType.BOOKING_CREATED,
                "Booking Created",
                "Your booking request has been created. Please confirm and pay to secure your session.",
                "/bookings/" + bookingId,
                bookingId);

        // For tutor
        createNotification(
                tutorId,
                NotificationType.NEW_STUDENT_REQUEST,
                "New Booking Request",
                "You have received a new booking request.",
                "/bookings/" + bookingId,
                bookingId);
    }

    public void notifyBookingConfirmed(String studentId, String tutorId, String bookingId) {
        createNotification(
                studentId,
                NotificationType.BOOKING_CONFIRMED,
                "Booking Confirmed",
                "Your booking has been confirmed and payment processed.",
                "/bookings/" + bookingId,
                bookingId);

        createNotification(
                tutorId,
                NotificationType.BOOKING_CONFIRMED,
                "Session Confirmed",
                "A student has confirmed their booking with you.",
                "/bookings/" + bookingId,
                bookingId);
    }

    public void notifyBookingCancelled(String studentId, String tutorId, String bookingId) {
        createNotification(
                studentId,
                NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled",
                "Your booking has been cancelled.",
                "/bookings/" + bookingId,
                bookingId);

        createNotification(
                tutorId,
                NotificationType.BOOKING_CANCELLED,
                "Session Cancelled",
                "A booking has been cancelled.",
                "/bookings/" + bookingId,
                bookingId);
    }

    public void notifyNewReview(String tutorId, String studentName, String reviewId) {
        createNotification(
                tutorId,
                NotificationType.NEW_REVIEW,
                "New Review",
                studentName + " has left you a review.",
                "/reviews/" + reviewId,
                reviewId);
    }

    public void notifyLowBalance(String studentId, double currentBalance) {
        createNotification(
                studentId,
                NotificationType.LOW_BALANCE_WARNING,
                "Low Balance Warning",
                String.format("Your balance is low ($%.2f). Add funds to continue booking sessions.", currentBalance));
    }

    public void notifySessionReminder(String userId, String sessionDetails, String bookingId) {
        createNotification(
                userId,
                NotificationType.BOOKING_REMINDER,
                "Upcoming Session Reminder",
                sessionDetails,
                "/bookings/" + bookingId,
                bookingId);
    }
}