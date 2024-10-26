package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    boolean existsRoleByName(String name);
    Role findRoleByName(String name);
}
