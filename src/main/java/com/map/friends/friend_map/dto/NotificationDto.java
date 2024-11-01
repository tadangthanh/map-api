package com.map.friends.friend_map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String recipientGoogleId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String senderGoogleId;
    private LocalDateTime expirationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long groupId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String senderAvatarUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String senderName;
}
