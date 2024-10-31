package com.map.friends.friend_map.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.map.friends.friend_map.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(NotificationDto notificationDto,String recipientFcmToken) {
        // Gửi thông báo đến thiết bị của người dùng
        Notification notification = Notification.builder()
                .setTitle(notificationDto.getTitle())
                .setBody(notificationDto.getMessage())
                .build();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(recipientFcmToken)
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            log.error("Lỗi khi gửi thông báo firebase: " + e.getMessage());
        }
    }
}
