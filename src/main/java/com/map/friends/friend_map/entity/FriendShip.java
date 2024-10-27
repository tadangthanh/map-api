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
    @Enumerated(EnumType.STRING)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author; // người gửi lời mời
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target; // người nhận lời mời
}
