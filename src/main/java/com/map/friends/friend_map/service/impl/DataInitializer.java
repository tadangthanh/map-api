package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.entity.Role;
import com.map.friends.friend_map.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepo roleRepository;

    @Override
    public void run(String... args) {
        List<Role> roles = this.initRole();
        roles.forEach((role) -> {
            if (!this.roleRepository.existsRoleByName(role.getName())) {
                this.roleRepository.save(role);
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
}
