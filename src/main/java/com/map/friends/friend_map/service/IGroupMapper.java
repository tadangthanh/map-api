package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupResponseDto;
import com.map.friends.friend_map.entity.Group;

import java.util.List;

public interface IGroupMapper extends IMapping<Group, GroupResponseDto> {
    Group toEntity(GroupRequestDto groupRequestDto);
    GroupResponseDto toResponseDto(Group group);
    List<GroupResponseDto> toResponseDtoList(List<Group> groups);
}
