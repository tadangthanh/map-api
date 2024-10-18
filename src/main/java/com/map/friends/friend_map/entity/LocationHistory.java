package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "location_history") // lịch sử vị trí
public class LocationHistory extends BaseEntity<Long>{
    private LocalDateTime timeStamp; // thời gian
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Location> location=new HashSet<>();
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
