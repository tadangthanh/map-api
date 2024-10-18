package com.map.friends.friend_map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity<Long>{
    private String message;
    private boolean isRead;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private NotificationType type;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
