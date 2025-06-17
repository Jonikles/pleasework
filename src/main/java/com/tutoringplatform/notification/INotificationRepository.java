package com.tutoringplatform.notification;

import com.tutoringplatform.shared.IRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface INotificationRepository extends IRepository<Notification> {
    List<Notification> findByUserId(String userId);
    List<Notification> findUnreadByUserId(String userId);
    List<Notification> findByUserIdAndType(String userId, NotificationType type);
    int countUnreadByUserId(String userId);
    void markAsRead(String notificationId);
    void markAllAsReadForUser(String userId);
    void deleteOldNotifications(LocalDateTime before);
    List<Notification> findRecentByUserId(String userId, int limit);
}