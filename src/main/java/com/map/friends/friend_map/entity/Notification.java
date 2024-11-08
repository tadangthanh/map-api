package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
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
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
    private boolean isRead;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(name = "expiration_date")
    @FutureOrPresent(message = "Expiration date must be in the future or present")
    private LocalDateTime expirationDate;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
