package com.map.friends.friend_map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location") // vị trí
public class Location extends BaseEntity<Long>{
    private  Double latitude; // vĩ độ
    private  Double longitude; // kinh độ
    private LocalDateTime timeStamp; // thời gian
    @OneToOne
    @JoinColumn(name = "user_id")
    private  User user;

}
