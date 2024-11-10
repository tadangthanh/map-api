package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.dto.request.GroupLocationRequest;
import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupLocationResponse;
import com.map.friends.friend_map.dto.response.GroupResponseDto;
import com.map.friends.friend_map.entity.*;
import com.map.friends.friend_map.exception.BadRequestException;
import com.map.friends.friend_map.exception.DuplicateGroupLocationException;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.exception.UnAuthorizeException;
import com.map.friends.friend_map.repository.*;
import com.map.friends.friend_map.service.IGroupMapper;
import com.map.friends.friend_map.service.IGroupService;
import com.map.friends.friend_map.service.ILocationMapper;
import com.map.friends.friend_map.service.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupRepo groupRepo;
    private final IGroupMapper groupMapperCustom;
    private final INotificationService notificationService;
    private final UserRepo userRepo;
    private final GroupHasLocationRepo groupHasLocationRepo;
    private final GroupRoleRepo groupRoleRepo;
    private final ILocationMapper locationMapper;
    private final LocationRepo locationRepo;
    private final UserHasGroupRepo userHasGroupRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserHasFriendRepo userHasFriendRepo;
    public static final int MAX_GROUP_CREATE_LIMIT = 3;
    public static final int MAX_GROUP_JOIN_LIMIT = 3;

    private void validateGroupCreateCount(User user) {
        int groupCreatedCount = groupRepo.countGroupByCreatedBy(user.getEmail());
        // 1 nguoi chi tao duoc toi da 3 group va tham gia toi da 3 group
        if (groupCreatedCount >= MAX_GROUP_CREATE_LIMIT) {
            throw new ResourceNotFoundException("You have reached the maximum number of groups you can create, which is " + MAX_GROUP_CREATE_LIMIT);
        }
    }

    private void validateGroupJoinCount(User user) {
        int groupJoinCount = userHasGroupRepo.countGroupsJoinedByUser(user.getId());
        if (groupJoinCount >= MAX_GROUP_JOIN_LIMIT) {
            throw new ResourceNotFoundException("You have reached the maximum number of groups you can join, which is " + MAX_GROUP_JOIN_LIMIT);
        }
    }

    private void validateFriend(User currentUser, User user) {
        if (!userHasFriendRepo.isFriendBetweenUserAAndUserB(currentUser.getId(), user.getId())) {
            throw new UnAuthorizeException("You are not friend with user " + user.getName());
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
            validateGroupJoinCount(user);
            // check if user is friend with current user
            validateFriend(currentUser, user);
            GroupRole groupRole = groupRoleRepo.findByName("VIEWER").orElseThrow(() -> new ResourceNotFoundException("Group role not found"));
            UserHasGroup userHasGroup = createUserHasGroup(user, group, UserGroupStatus.PENDING, groupRole);
            // send notification
            notificationService.createNotificationToUser(user.getGoogleId(), group.getName() == null ? "Lời mời tham gia nhóm" : group.getName(), "Baạn có 1 lời mời tham gia nhóm", NotificationType.GROUP_INVITATION);
        }
        // user create group is admin and status is joined
        GroupRole groupRole = groupRoleRepo.findByName("ADMIN").orElseThrow(() -> new ResourceNotFoundException("Group role not found"));
        UserHasGroup userHasGroup = createUserHasGroup(currentUser, group, UserGroupStatus.JOINED, groupRole);
        sendGroupsCreateToWs(group);
        return groupMapperCustom.toResponseDto(group);
    }

    private UserHasGroup createUserHasGroup(User user, Group group, UserGroupStatus status, GroupRole groupRole) {
        UserHasGroup userHasGroup = new UserHasGroup();
        userHasGroup.setUser(user);
        userHasGroup.setGroup(group);
        userHasGroup.setStatus(status);
        userHasGroup.setGroupRole(groupRole);
        group.addMember(userHasGroup);
        return userHasGroupRepo.saveAndFlush(userHasGroup);
    }

    // khi nguoi dung tao thanh cong nhom thi se tra ve nhom moi bang websocket de nguoi dung lang nghe su kien cua gr do
    private void sendGroupsCreateToWs(Group group) {
        group.getUsers().forEach(userHasGroup -> {
            if (userHasGroup.getStatus().equals(UserGroupStatus.JOINED)) {
                simpMessagingTemplate.convertAndSendToUser(userHasGroup.getUser().getEmail(), "/private/group-create", groupMapperCustom.toResponseDto(group));
            }
        });
    }

    private void sendGroupDisbandToWs(Long groupId) {
        simpMessagingTemplate.convertAndSend("/topic/group-disband/" + groupId, groupId);
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
        sendGroupDisbandToWs(groupId);
    }

    @Override
    public GroupResponseDto acceptGroupJoinRequest(Long groupId) {
        User currentUser = getCurrentUser();
        UserHasGroup userHasGroup = userHasGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId).orElseThrow(() -> new ResourceNotFoundException("User has not joined this group"));
        userHasGroup.setStatus(UserGroupStatus.JOINED);
        sendGroupsCreateToWs(userHasGroup.getGroup());
        return groupMapperCustom.toResponseDto(userHasGroup.getGroup());
    }

    @Override
    public void rejectGroupJoinRequest(Long groupId) {
        User currentUser = getCurrentUser();
        UserHasGroup userHasGroup = userHasGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId).orElseThrow(() -> new ResourceNotFoundException("User has not joined this group"));
        notificationService.deleteNotificationByGroup(groupId);
        userHasGroupRepo.delete(userHasGroup);
    }

    @Override
    public List<GroupLocationResponse> addLocationToGroups(GroupLocationRequest groupLocationRequest) {
        List<GroupLocationResponse> result = new ArrayList<>();
        User currentUser = getCurrentUser();

        for (Long groupId : groupLocationRequest.getGroupIds()) {
            Group group = groupRepo.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            validateGroupMembership(currentUser, group);
            GroupHasLocation groupHasLocation = addLocationToGroup(group, groupLocationRequest.getLocation());
            List<Location> locations = getLocationsOfGroup(groupId);
            result.add(createGroupLocationResponse(groupId, group, locations));
            notifyGroupOfNewLocation(group);
        }
        // send group location updates to all group members
        sendGroupLocationAdd(groupLocationRequest.getGroupIds(), result);
        return result;
    }

    private GroupLocationResponse createGroupLocationResponse(Long groupId, Group group, List<Location> locations) {
        return GroupLocationResponse.builder().groupId(groupId).groupName(group.getName()).locations(locationMapper.toDtoList(locations)).build();
    }

    private void notifyGroupOfNewLocation(Group group) {
        String notificationTitle = group.getName() == null ? "Đánh dấu địa điểm" : group.getName();
        notificationService.createNotificationToGroup(group.getId(), notificationTitle, "Có một địa điểm mới được đánh dấu", NotificationType.LOCATION_MARKED);
    }

    // gui vi tri moi duoc them vao nhom den tat ca thanh vien cua nhom
    private void sendGroupLocationAdd(List<Long> groupIds, List<GroupLocationResponse> result) {
        groupIds.forEach(groupId -> simpMessagingTemplate.convertAndSend("/topic/group-location-add/" + groupId, result));
    }

    private List<Location> getLocationsOfGroup(Long groupId) {
        List<GroupHasLocation> groupHasLocations = groupHasLocationRepo.findAllByGroupId(groupId);
        return groupHasLocations.stream().map(GroupHasLocation::getLocation).toList();
    }

    private GroupHasLocation addLocationToGroup(Group group, LocationDto locationDto) {
        Location location = locationMapper.toEntity(locationDto);
        location = locationRepo.saveAndFlush(location);

        GroupHasLocation groupHasLocation = new GroupHasLocation();
        groupHasLocation.setGroup(group);
        groupHasLocation.setLocation(location);
        return groupHasLocationRepo.saveAndFlush(groupHasLocation);
    }

    private void validateGroupMembership(User currentUser, Group group) {
        if (groupHasLocationRepo.existByCreatedByAndGroupId(currentUser.getEmail(), group.getId())) {
            throw new DuplicateGroupLocationException("Mỗi người chỉ có thể đánh dấu một địa điểm cho mỗi nhóm.");
        }
        if (!userHasGroupRepo.existsByUserIdAndGroupId(currentUser.getId(), group.getId())) {
            throw new UnAuthorizeException("You are not a member of this group");
        }
    }

    @Override
    public List<GroupLocationResponse> getGroupLocations() {
        User currentUser = getCurrentUser();
        List<UserHasGroup> userHasGroups = userHasGroupRepo.findAllByUserId(currentUser.getId());
        List<Group> groups = userHasGroups.stream().map(UserHasGroup::getGroup).toList();
        List<GroupLocationResponse> result = new ArrayList<>();
        for (Group group : groups) {
            List<GroupHasLocation> groupHasLocations = groupHasLocationRepo.findAllByGroupId(group.getId());
            List<Location> locations = groupHasLocations.stream().map(GroupHasLocation::getLocation).toList();
            result.add(GroupLocationResponse.builder().groupId(group.getId()).groupName(group.getName()).locations(locationMapper.toDtoList(locations)).build());
        }
        return result;
    }

    @Override
    public void deleteLocation(Long locationId) {
        Location location = locationRepo.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        GroupHasLocation groupHasLocation = groupHasLocationRepo.findByLocationId(locationId).orElseThrow(() -> new ResourceNotFoundException("Group location not found"));
        User currentUser = getCurrentUser();
        if (!groupHasLocation.getCreatedBy().equals(currentUser.getEmail()) && !location.getCreatedBy().equals(currentUser.getEmail())) {
            throw new BadRequestException("You are not authorized to delete this location");
        }
        groupHasLocationRepo.delete(groupHasLocation);
        locationRepo.delete(location);
        sendLocationDeletedToWs(groupHasLocation.getGroup().getId(), locationId);
    }

    private void sendLocationDeletedToWs(Long groupId, Long locationId) {
        simpMessagingTemplate.convertAndSend("/topic/group-location-deleted/" + groupId, locationId);
    }


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
