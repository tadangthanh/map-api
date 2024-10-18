package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friend_ship")// lời mời kết bạn, 1 người dùng có thể gửi nhiều lời mời kết bạn, nhận nhiều lời mời kết bạn
public class FriendShip extends BaseEntity<Long>{
    private FriendShipStatus status; // trạng thái lời mời kết bạn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // người gửi lời mời
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver; // người nhận lời mời
}
