package com.map.friends.friend_map.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "group_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupRole extends BaseEntity<Integer>{
    private String name;
    @OneToMany(mappedBy = "groupRole",cascade = CascadeType.ALL)
    private List<GroupRolePermission> groupRolePermissions;
}
