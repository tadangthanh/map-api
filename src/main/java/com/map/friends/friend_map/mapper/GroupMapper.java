package com.map.friends.friend_map.mapper;

import com.map.friends.friend_map.dto.request.GroupRequestDto;
import com.map.friends.friend_map.dto.response.GroupResponseDto;
import com.map.friends.friend_map.entity.Group;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface GroupMapper {
   Group toEntity(GroupRequestDto groupRequestDto);
   GroupResponseDto toResponseDto(Group group);
}
