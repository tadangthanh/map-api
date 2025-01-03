package com.map.friends.friend_map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_has_location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupHasLocation extends BaseEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;
}
