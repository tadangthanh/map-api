package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.entity.GroupRole;
import com.map.friends.friend_map.entity.GroupRolePermission;
import com.map.friends.friend_map.entity.Permission;
import com.map.friends.friend_map.entity.Role;
import com.map.friends.friend_map.repository.GroupRolePermissionRepo;
import com.map.friends.friend_map.repository.GroupRoleRepo;
import com.map.friends.friend_map.repository.PermissionRepo;
import com.map.friends.friend_map.repository.RoleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {
    private final RoleRepo roleRepository;
    private final GroupRoleRepo groupRoleRepo;
    private final PermissionRepo permissionRepo;
    private final GroupRolePermissionRepo groupRolePermissionRepo;

    @Override
    public void run(String... args) {
        List<Role> roles = this.initRole();
        roles.forEach((role) -> {
            if (!this.roleRepository.existsRoleByName(role.getName())) {
                this.roleRepository.save(role);
            }
        });
        List<Permission> permissions = this.initPermission();
        permissions.forEach((permission) -> {
            if (!this.permissionRepo.existsPermissionByName(permission.getName())) {
                this.permissionRepo.save(permission);
            }
        });
        List<GroupRole> groupRoles = this.initGroupRole(permissions);
        groupRoles.forEach((groupRole) -> {
            if (!this.groupRoleRepo.existsGroupRoleByName(groupRole.getName())) {
                this.groupRoleRepo.save(groupRole);
            }
        });
    }

    private List<Role> initRole() {
        Role r1 = new Role();
        r1.setName("ROLE_ADMIN");
        Role r2 = new Role();
        r2.setName("ROLE_USER");
        return List.of(r1, r2);
    }

    private List<Permission> initPermission() {
        Permission p1 = new Permission();
        p1.setName("ADD");
        Permission p2 = new Permission();
        p2.setName("EDIT");
        Permission p3 = new Permission();
        p3.setName("DELETE");
        Permission p4 = new Permission();
        p4.setName("VIEW");
        return List.of(p1, p2, p3, p4);
    }

    private List<GroupRole> initGroupRole(List<Permission> permissions) {
        // Lấy các quyền từ danh sách permissions
        Permission addPermission = permissions.stream().filter(p -> p.getName().equals("ADD")).findFirst().orElse(null);
        Permission editPermission = permissions.stream().filter(p -> p.getName().equals("EDIT")).findFirst().orElse(null);
        Permission deletePermission = permissions.stream().filter(p -> p.getName().equals("DELETE")).findFirst().orElse(null);
        Permission viewPermission = permissions.stream().filter(p -> p.getName().equals("VIEW")).findFirst().orElse(null);
        GroupRole gr1 = new GroupRole();
        gr1.setName("ADMIN");
        gr1.setGroupRolePermissions(List.of(
                new GroupRolePermission(gr1, addPermission),
                new GroupRolePermission(gr1, editPermission),
                new GroupRolePermission(gr1, deletePermission),
                new GroupRolePermission(gr1, viewPermission)
        ));
        GroupRole gr2 = new GroupRole();
        gr2.setName("MANAGER");
        gr2.setGroupRolePermissions(List.of(
                new GroupRolePermission(gr2, addPermission),
                new GroupRolePermission(gr2, editPermission),
                new GroupRolePermission(gr2, viewPermission)
        ));
        GroupRole gr3 = new GroupRole();
        gr3.setName("VIEWER");
        gr3.setGroupRolePermissions(List.of(
                new GroupRolePermission(gr3, viewPermission)
        ));
        return List.of(gr1, gr2, gr3);
    }
}
