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
@Table(name = "sos")
public class Sos extends BaseEntity<Integer>{
    private String message;
    private boolean isResolved;
    @OneToOne
    @JoinColumn(name = "location_id")
    private  Location location;
    @OneToOne
    @JoinColumn(name = "user_id")
    private  User user;
}
