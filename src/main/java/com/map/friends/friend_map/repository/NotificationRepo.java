package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.Notification;
import com.map.friends.friend_map.entity.NotificationType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification,Long> {
    @Modifying
    @Transactional
    @Query("delete from Notification n where n.group.id = ?1")
    void deleteByGroupId(Long id);
    @Modifying
    @Transactional
    @Query("delete from Notification n where n.sender.id = ?1 and n.recipient.id = ?2 and n.type = ?3")
    void deleteBySenderAndRecipientAndType(Long senderId, Long recipientId, NotificationType type);
    @Query("select n from Notification n where n.recipient.id = ?1")
    Page<Notification> findAllByRecipientId(Long recipientId, Pageable pageable);
    @Transactional
    @Modifying
    @Query("update Notification n set n.isRead = true where n.recipient.id = ?1")
    void markAsReadByRecipientId(Long recipientId);

    @Modifying
    @Transactional
    @Query("delete from Notification n where n.recipient.id = ?1")
    void deleteAllByRecipientId(Long recipientId);
    @Query("select n from Notification n where n.recipient.id = ?1 and n.id = ?2")
    Optional<Notification> findByRecipientIdAndId(Long recipientId, Long id);

    @Query("select count(n) from Notification n where n.recipient.id = ?1 and n.isRead = false")
    int countUnreadNotification(Long recipientId);

}
