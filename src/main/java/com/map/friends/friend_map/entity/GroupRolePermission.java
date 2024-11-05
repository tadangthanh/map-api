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
@Entity
@Table(name = "group_role_permission")
@AllArgsConstructor
@NoArgsConstructor
public class GroupRolePermission extends BaseEntity<Integer>{
    @ManyToOne
    @JoinColumn(name = "group_role_id")
    private GroupRole groupRole;
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

}
