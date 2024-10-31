package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity<Long>{
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 500)
    private String message;
    private boolean isRead;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
