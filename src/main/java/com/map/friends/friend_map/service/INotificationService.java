package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.entity.NotificationType;

public interface INotificationService {
    NotificationDto createNotification(NotificationDto notificationDto);
    void deleteNotificationByGroup(Long id);
    void deleteBySenderRecipientAndType(Long senderId, Long recipientId, NotificationType type);
    PageResponse<?> getNotifications(int page, int size);
    void markAsReadAll();

    void deleteAllNotification();
    NotificationDto markAsRead(Long id);

    int countUnreadNotification();
}
