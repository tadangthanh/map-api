package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.GroupRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRolePermissionRepo extends JpaRepository<GroupRolePermission,Integer> {
    @Query("select case when count(grp)> 0 then true else false end from GroupRolePermission grp where grp.groupRole.id = :groupRoleId and grp.permission.name = :permissionName")
    boolean existsByGroupRoleIdAndPermissionName(int groupRoleId, String permissionName);
}
