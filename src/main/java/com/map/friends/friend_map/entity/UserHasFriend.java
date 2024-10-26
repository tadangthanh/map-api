package com.map.friends.friend_map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_has_friend") // 1 ng dùng có thể có nhiều bạn bè, và phải 2 ng dùng đều đồng ý mới là bạn bè
@Entity
public class UserHasFriend extends BaseEntity<Long>{
    @ManyToOne
    @JoinColumn(name = "user_a_id")
    private User userA;

    @ManyToOne
    @JoinColumn(name = "user_b_id")
    private User userB;
}
