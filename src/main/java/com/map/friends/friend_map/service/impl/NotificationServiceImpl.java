package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.entity.Group;
import com.map.friends.friend_map.entity.Notification;
import com.map.friends.friend_map.entity.NotificationType;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.mapper.NotificationMapper;
import com.map.friends.friend_map.repository.GroupRepo;
import com.map.friends.friend_map.repository.NotificationRepo;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    private final NotificationMapper notificationMapper;
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final GroupRepo groupRepo;


    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = notificationMapper.toEntity(notificationDto);
        User sender = userRepo.findByGoogleId(notificationDto.getSenderGoogleId()).orElseThrow(() -> new ResourceNotFoundException("Sender notification not found"));
        notification.setSender(sender);
        notification.setRead(false);
        if (notificationDto.getGroupId() != null) {
            Group group = groupRepo.findById(notificationDto.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            notification.setGroup(group);
            // Nếu thông báo là cho nhóm, không cần chỉ định recipient
            notification.setRecipient(null);
        } else if (notificationDto.getRecipientGoogleId() != null) {
            User recipient = userRepo.findByGoogleId(notificationDto.getRecipientGoogleId()).orElseThrow(() -> new ResourceNotFoundException("recipient not found"));
            notification.setRecipient(recipient);
            firebaseMessagingService.sendNotification(notificationDto, recipient.getFcmToken());
            // Nếu thông báo là cho cá nhân, không cần chỉ định group
            notification.setGroup(null);
        } else {
            throw new IllegalArgumentException("The recipient or group must be specified");
        }
        notificationRepo.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public void deleteNotificationByGroup(Long id) {
        notificationRepo.deleteByGroupId(id);
    }

    @Override
    public void deleteBySenderRecipientAndType(Long senderId, Long recipientId, NotificationType type) {
        notificationRepo.deleteBySenderAndRecipientAndType(senderId,recipientId,type);
    }

    private User getCurrentUser() {
        return userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
