package com.tutoringplatform.shared.dto.response;

import java.util.List;

import com.tutoringplatform.notification.Notification;

public class NotificationListResponse {
    private List<Notification> notifications;
    private int unreadCount;
    private int totalCount;

    // Getters and setters
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        this.totalCount = notifications != null ? notifications.size() : 0;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}