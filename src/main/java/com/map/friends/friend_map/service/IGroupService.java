package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupResponseDto;

import java.util.List;

public interface IGroupService {
    GroupResponseDto createGroup(GroupRequestDto groupRequestDto);

    List<GroupResponseDto> getGroups();

    void disbandGroup(Long groupId);

    GroupResponseDto acceptGroupJoinRequest( Long groupId);
    void rejectGroupJoinRequest( Long groupId);
}

