package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
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
public class Group extends BaseEntity<Long>{
    private String name;
    private String description;
    @OneToMany(mappedBy = "group")
    private Set<UserHasGroup> users= new HashSet<>();
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location markedLocation;
}
