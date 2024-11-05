package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupResponseDto;
import com.map.friends.friend_map.entity.*;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.mapper.GroupMapper;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.IGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupMapperCustom implements IGroupMapper {
    private final GroupMapper groupMapper;
    private final UserRepo userRepo;
    @Override
    public GroupResponseDto toDto(Group group) {
        return null;
    }

    @Override
    public Group toEntity(GroupResponseDto groupResponseDto) {
        return null;
    }

    @Override
    public Group toEntity(GroupRequestDto groupRequestDto) {
        return groupMapper.toEntity(groupRequestDto);
    }

    @Override
    public GroupResponseDto toResponseDto(Group group) {
        GroupResponseDto groupResponseDto = groupMapper.toResponseDto(group);
        Set<UserHasGroup> userHasGroups = group.getUsers();
        User currentUser = getCurrentUser();
        userHasGroups.forEach(uhg -> {
            if(uhg.getGroup().getId().equals(group.getId()) && uhg.getUser().getId().equals(currentUser.getId())) {
              GroupRole groupRole = uhg.getGroupRole();
              // set role and permissions for current user
              List<GroupRolePermission> groupRolePermissions = groupRole.getGroupRolePermissions();
                List<Permission> permissions = groupRolePermissions.stream().map(GroupRolePermission::getPermission).toList();
                List<String> permissionNames = permissions.stream().map(Permission::getName).toList();
                groupResponseDto.setPermissions(permissionNames);
                groupResponseDto.setRole(groupRole.getName());
            }
        });
        UserHasGroup userHasGroup= userHasGroups.stream().filter((s)->s.getGroup().getId().equals(groupResponseDto.getId()) && s.getUser().getId().equals(currentUser.getId())).findFirst().orElseThrow(()->new ResourceNotFoundException("you don't joined or invite group"));
        groupResponseDto.setStatus(userHasGroup.getStatus());
        groupResponseDto.setTotalMembers(userHasGroups.size());
        return groupResponseDto;
    }

    @Override
    public List<GroupResponseDto> toResponseDtoList(List<Group> groups) {
        return groups.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
