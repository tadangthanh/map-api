package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.entity.NotificationType;

public interface INotificationService {
    NotificationDto createNotification(NotificationDto notificationDto);
    void deleteNotificationByGroup(Long id);
    void deleteBySenderRecipientAndType(Long senderId, Long recipientId, NotificationType type);
}
