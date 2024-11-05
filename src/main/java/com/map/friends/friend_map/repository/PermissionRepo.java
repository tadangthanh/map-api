package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepo extends JpaRepository<Permission,Integer> {
    boolean existsPermissionByName(String name);
}
