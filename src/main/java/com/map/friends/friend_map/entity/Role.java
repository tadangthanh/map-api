package com.map.friends.friend_map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role extends BaseEntity<Integer> implements GrantedAuthority {
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
