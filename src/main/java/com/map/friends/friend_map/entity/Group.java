package com.map.friends.friend_map.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_group") // nhóm
public class Group extends BaseEntity<Long> {
    private String name;
    private String description;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<UserHasGroup> users = new HashSet<>();
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<GroupHasLocation> groupHasLocations;

    public void addMember(UserHasGroup userHasGroup) {
        users.add(userHasGroup);
    }
}
