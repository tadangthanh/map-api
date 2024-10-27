package com.map.friends.friend_map.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> implements UserDetails {
    @Column(name = "google_id", unique = true)
    private String googleId;
    private String name;
    private String password;
    @Column(name = "email", unique = true)
    private String email;
    private boolean isLocationSharing;
    private Integer batteryLevel;
    private String avatarUrl;
    @OneToMany(mappedBy = "user")
    private Set<UserHasGroup> groups = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "userA")
    private Set<UserHasFriend> friendsAsUserA = new HashSet<>(); // Quan hệ khi người dùng đóng vai trò là userA

    @OneToMany(mappedBy = "userB")
    private Set<UserHasFriend> friendsAsUserB = new HashSet<>(); // Quan hệ khi người dùng đóng vai trò là userB

    @OneToMany(mappedBy = "target")
    private Set<FriendShip> receivedFriendShips = new HashSet<>(); // Quan hệ khi người dùng nhận lời mời kết bạn

    @OneToMany(mappedBy = "author")
    private Set<FriendShip> sentFriendShips  = new HashSet<>(); // Quan hệ khi người dùng gửi lời mời kết bạn



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
           return List.of(role);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }
}
