package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.Notification;
import com.map.friends.friend_map.entity.NotificationType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
