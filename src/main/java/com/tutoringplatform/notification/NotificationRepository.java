package com.tutoringplatform.notification;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository implements INotificationRepository {
    private Map<String, Notification> notifications = new HashMap<>();
    
    @Override
    public Notification findById(String id) {
        return notifications.get(id);
    }
    
    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }
    
    @Override
    public List<Notification> findByUserId(String userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Notification> findUnreadByUserId(String userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Notification> findByUserIdAndType(String userId, NotificationType type) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && n.getType() == type)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    @Override
    public int countUnreadByUserId(String userId) {
        return (int) notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }
    
    @Override
    public void markAsRead(String notificationId) {
        Notification notification = notifications.get(notificationId);
        if (notification != null) {
            notification.setRead(true);
            notifications.put(notificationId, notification);
        }
    }
    
    @Override
    public void markAllAsReadForUser(String userId) {
        notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .forEach(n -> n.setRead(true));
    }
    
    @Override
    public void deleteOldNotifications(LocalDateTime before) {
        notifications.entrySet().removeIf(entry -> 
            entry.getValue().getCreatedAt().isBefore(before));
    }
    
    @Override
    public List<Notification> findRecentByUserId(String userId, int limit) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public void save(Notification notification) {
        notifications.put(notification.getId(), notification);
    }
    
    @Override
    public void update(Notification notification) {
        notifications.put(notification.getId(), notification);
    }
    
    @Override
    public void delete(String id) {
        notifications.remove(id);
    }
}
