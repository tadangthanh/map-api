package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRoleRepo extends JpaRepository<GroupRole,Integer> {
    boolean existsGroupRoleByName(String name);
    @Query("select g from GroupRole g where g.name = :name")
    Optional<GroupRole> findByName(String name);
}
