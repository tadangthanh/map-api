package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.entity.GroupRole;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.entity.UserHasGroup;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.GroupRolePermissionRepo;
import com.map.friends.friend_map.repository.UserHasGroupRepo;
import com.map.friends.friend_map.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupSecurityService {
    private final UserHasGroupRepo userHasGroupRepo;
    private final UserRepo userRepo;
    private final GroupRolePermissionRepo groupRolePermissionRepo;
    // Kiểm tra quyền của người dùng trong nhóm
    public boolean hasPermission(Authentication authentication, Long groupId, String permission) {
        String email = authentication.getName(); // Lấy tên người dùng từ Authentication
        User currentUser = userRepo.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("You don't have permission to access this group"));
        // Lấy vai trò của người dùng trong nhóm từ bảng user_group_roles
        UserHasGroup userHasGroup = userHasGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId).orElseThrow(()->new ResourceNotFoundException("You don't have permission to access this group"));
        GroupRole userRole = userHasGroup.getGroupRole();
        if (userRole == null) {
            return false;
        }
        // Kiểm tra quyền của vai trò trong bảng group_role_permission
        return groupRolePermissionRepo.existsByGroupRoleIdAndPermissionName(userRole.getId(), permission);
    }
}
