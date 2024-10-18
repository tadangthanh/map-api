package com.map.friends.friend_map.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends BaseEntity<Long>{
   private String facebookId;
    private String name;
    private String password;
    private String email;
    private boolean isLocationSharing;
    private Integer batteryLevel;
    @OneToMany(mappedBy = "user")
    private Set<UserHasGroup> groups = new HashSet<>();
    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
