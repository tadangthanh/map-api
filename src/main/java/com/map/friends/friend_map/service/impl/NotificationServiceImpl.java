package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.dto.response.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public NotificationDto createNotification(
            String senderGoogleId,
            String recipientGoogleId,
            Long groupId,
            String title,
            String message,
            NotificationType type) {

        NotificationDto notificationDto = buildNotificationDto(senderGoogleId, recipientGoogleId, groupId, title, message, type);
        Notification notification = mapToEntity(notificationDto);

        setNotificationSender(notification, senderGoogleId);
        setNotificationRecipientAndGroup(notification, notificationDto);

        notificationRepo.save(notification);
        return notificationMapper.toDto(notification);
    }

    private NotificationDto buildNotificationDto(
            String senderGoogleId,
            String recipientGoogleId,
            Long groupId,
            String title,
            String message,
            NotificationType type) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setSenderGoogleId(senderGoogleId);
        notificationDto.setRecipientGoogleId(recipientGoogleId);
        notificationDto.setGroupId(groupId);
        notificationDto.setTitle(title);
        notificationDto.setMessage(message);
        notificationDto.setType(type);
        notificationDto.setExpirationDate(LocalDateTime.now().plusDays(7));
        return notificationDto;
    }

    private Notification mapToEntity(NotificationDto notificationDto) {
        Notification notification = notificationMapper.toEntity(notificationDto);
        notification.setRead(false);
        return notification;
    }

    private void setNotificationSender(Notification notification, String senderGoogleId) {
        User sender = userRepo.findByGoogleId(senderGoogleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender notification not found"));
        notification.setSender(sender);
    }

    private void setNotificationRecipientAndGroup(Notification notification, NotificationDto notificationDto) {
        if (notificationDto.getGroupId() != null && notificationDto.getRecipientGoogleId() != null) {
            setNotificationGroup(notification, notificationDto.getGroupId());
            setNotificationRecipient(notification, notificationDto.getRecipientGoogleId());
        } else if (notificationDto.getGroupId() != null) {
            setNotificationGroup(notification, notificationDto.getGroupId());
            notification.setRecipient(null);  // Group notification, no recipient needed
        } else if (notificationDto.getRecipientGoogleId() != null) {
            setNotificationRecipient(notification, notificationDto.getRecipientGoogleId());
            notification.setGroup(null);  // Individual notification, no group needed
        } else {
            throw new IllegalArgumentException("Either recipient or group must be specified");
        }
    }

    private void setNotificationGroup(Notification notification, Long groupId) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        notification.setGroup(group);
    }

    private void setNotificationRecipient(Notification notification, String recipientGoogleId) {
        User recipient = userRepo.findByGoogleId(recipientGoogleId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));
        notification.setRecipient(recipient);
        firebaseMessagingService.sendNotification(notificationMapper.toDto(notification), recipient.getFcmToken());
    }


    @Override
    public void deleteNotificationByGroup(Long id) {
        notificationRepo.deleteByGroupId(id);
    }

    @Override
    public void deleteBySenderRecipientAndType(Long senderId, Long recipientId, NotificationType type) {
        notificationRepo.deleteBySenderAndRecipientAndType(senderId, recipientId, type);
    }

    @Override
    public PageResponse<?> getNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User currentUser = getCurrentUser();
        Page<Notification> pageNotification = notificationRepo.findAllByRecipientId(currentUser.getId(), pageable);
        List<NotificationDto> notificationDtoList = notificationMapper.toDtoList(pageNotification.getContent());
        return PageResponse.builder()
                .items(notificationDtoList)
                .totalItems(pageNotification.getTotalElements())
                .totalPage(pageNotification.getTotalPages())
                .hasNext(pageNotification.hasNext())
                .pageNo(page)
                .pageSize(size)
                .build();
    }

    @Override
    public void markAsReadAll() {
        User currentUser = getCurrentUser();
        notificationRepo.markAsReadByRecipientId(currentUser.getId());
    }

    @Override
    public void deleteAllNotification() {
        User currentUser = getCurrentUser();
        notificationRepo.deleteAllByRecipientId(currentUser.getId());
    }

    @Override
    public NotificationDto markAsRead(Long id) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepo.findByRecipientIdAndId(currentUser.getId(), id).orElseThrow(() -> new ResourceNotFoundException("Notification not found or not belong to current user"));
        notification.setRead(true);
        notificationRepo.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public int countUnreadNotification() {
        User currentUser = getCurrentUser();
        return notificationRepo.countUnreadNotification(currentUser.getId());
    }

    @Override
    public void deleteNotification(Long id) {
        User currentUser = getCurrentUser();
        notificationRepo.deleteByRecipientIdAndId(currentUser.getId(), id);
    }

    @Override
    public void deleteNotificationByGroupId(Long groupId) {
        notificationRepo.deleteByGroupId(groupId);
    }

    private User getCurrentUser() {
        return userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
