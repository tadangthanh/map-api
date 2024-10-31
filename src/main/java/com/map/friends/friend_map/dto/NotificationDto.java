package com.map.friends.friend_map.dto;

import com.map.friends.friend_map.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto extends BaseDto<Long>{
    private String title;
    private String message;
    private boolean isRead;
    private NotificationType type;
    private String recipientGoogleId;
    private String senderGoogleId;
    private LocalDateTime expirationDate;
    private Long groupId;
}
