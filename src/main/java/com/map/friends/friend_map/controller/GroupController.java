package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.service.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {
    private final IGroupService groupService;

    @PostMapping
    public ResponseData<?> createGroup(@RequestBody GroupRequestDto groupRequestDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create group successfully", groupService.createGroup(groupRequestDto));
    }
    @GetMapping
    public ResponseData<?> getGroups() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get groups successfully", groupService.getGroups());
    }
    @PreAuthorize("@groupSecurityService.hasPermission(authentication, #groupId, 'ADD')")
    @PostMapping("/{groupId}/add-member")
    public ResponseData<?> addMember(@PathVariable("groupId") Long groupId, @RequestParam Long userId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Member added successfully", null);
    }

    @DeleteMapping("/{groupId}")
    public ResponseData<?> disbandGroup(@PathVariable("groupId") Long groupId) {
        groupService.disbandGroup(groupId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Disband group successfully", null);
    }
    @PatchMapping("/{groupId}/accept-join-request")
    public ResponseData<?> acceptGroupJoinRequest(@PathVariable("groupId") Long groupId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Accept join request successfully", groupService.acceptGroupJoinRequest(groupId));
    }

    @DeleteMapping("/{groupId}/reject-join-request")
    public ResponseData<?> rejectGroupJoinRequest(@PathVariable("groupId") Long groupId) {
        groupService.rejectGroupJoinRequest(groupId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Reject join request successfully", null);
    }
}
