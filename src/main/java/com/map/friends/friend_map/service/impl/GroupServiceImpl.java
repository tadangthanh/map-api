package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupResponseDto;
import com.map.friends.friend_map.entity.*;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.GroupRepo;
import com.map.friends.friend_map.repository.GroupRoleRepo;
import com.map.friends.friend_map.repository.UserHasGroupRepo;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.IGroupMapper;
import com.map.friends.friend_map.service.IGroupService;
import com.map.friends.friend_map.service.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupRepo groupRepo;
    private final IGroupMapper groupMapperCustom;
    private final INotificationService notificationService;
    private final UserRepo userRepo;
    private final GroupRoleRepo groupRoleRepo;
    private final UserHasGroupRepo userHasGroupRepo;
    public static final int MAX_GROUP_CREATE_LIMIT = 3;
    public static final int MAX_GROUP_JOIN_LIMIT = 3;

    private void validateGroupCreateCount(User user) {
        int groupCreatedCount = groupRepo.countGroupByCreatedBy(user.getEmail());
        // 1 nguoi chi tao duoc toi da 3 group va tham gia toi da 3 group
        if (groupCreatedCount >= MAX_GROUP_CREATE_LIMIT) {
            throw new ResourceNotFoundException("You have reached the maximum number of groups you can create, which is " + MAX_GROUP_CREATE_LIMIT);
        }
    }

    @Override
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto) {
        User currentUser = getCurrentUser();
        // 1 nguoi chi tao duoc toi da 3 group
        validateGroupCreateCount(currentUser);
        Group group = groupMapperCustom.toEntity(groupRequestDto);
        group = groupRepo.saveAndFlush(group);
        for (Long id : groupRequestDto.getUserIds()) {
            User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found user with id: " + id));
            // 1 nguoi chi tham gia toi da 3 group
            if (userHasGroupRepo.countGroupsJoinedByUser(user.getId()) >= MAX_GROUP_JOIN_LIMIT) {
                throw new ResourceNotFoundException("User with name " + user.getName() + " has reached the maximum number of groups they can join, which is " + MAX_GROUP_JOIN_LIMIT);
            }
            UserHasGroup userHasGroup = new UserHasGroup();
            userHasGroup.setGroup(group);
            userHasGroup.setUser(user);
            userHasGroup.setStatus(UserGroupStatus.PENDING);
            userHasGroupRepo.saveAndFlush(userHasGroup);
            GroupRole groupRole = groupRoleRepo.findByName("VIEWER").orElseThrow(() -> new ResourceNotFoundException("Group role not found"));
            userHasGroup.setGroupRole(groupRole);
            group.addMember(userHasGroup);
            // send notification
            notificationService.createNotification(currentUser.getGoogleId(), user.getGoogleId(), group.getId(), group.getName() == null ? "Lời mời tham gia nhóm" : group.getName(), "Baạn có 1 lời mời tham gia nhóm", NotificationType.GROUP_INVITATION);
        }
        // user create group is admin and status is joined
        UserHasGroup userHasGroup = new UserHasGroup();
        userHasGroup.setGroup(group);
        userHasGroup.setStatus(UserGroupStatus.JOINED);
        userHasGroup.setUser(currentUser);
        userHasGroupRepo.saveAndFlush(userHasGroup);
        group.addMember(userHasGroup);

        // assign group creator role to group
        GroupRole groupRole = groupRoleRepo.findByName("ADMIN").orElseThrow(() -> new ResourceNotFoundException("Group role not found"));
        userHasGroup.setGroupRole(groupRole);
        userHasGroupRepo.saveAndFlush(userHasGroup);
        return groupMapperCustom.toResponseDto(group);
    }

    @Override
    public List<GroupResponseDto> getGroups() {
        User currentUser = getCurrentUser();
        List<UserHasGroup> userHasGroups = userHasGroupRepo.findAllByUserId(currentUser.getId());
        return groupMapperCustom.toResponseDtoList(userHasGroups.stream().map(UserHasGroup::getGroup).toList());
    }

    @Override
    @PreAuthorize("@groupSecurityService.hasPermission(authentication, #groupId, 'DELETE')")
    public void disbandGroup(Long groupId) {
        Group group = groupRepo.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        notificationService.deleteNotificationByGroup(groupId);
        groupRepo.delete(group);
    }

    @Override
    public GroupResponseDto acceptGroupJoinRequest(Long groupId) {
        User currentUser = getCurrentUser();
        UserHasGroup userHasGroup = userHasGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId).orElseThrow(() -> new ResourceNotFoundException("User has not joined this group"));
        userHasGroup.setStatus(UserGroupStatus.JOINED);
        return groupMapperCustom.toResponseDto(userHasGroup.getGroup());
    }

    @Override
    public void rejectGroupJoinRequest(Long groupId) {
        User currentUser = getCurrentUser();
        UserHasGroup userHasGroup = userHasGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId).orElseThrow(() -> new ResourceNotFoundException("User has not joined this group"));
        notificationService.deleteNotificationByGroup(groupId);
        userHasGroupRepo.delete(userHasGroup);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
