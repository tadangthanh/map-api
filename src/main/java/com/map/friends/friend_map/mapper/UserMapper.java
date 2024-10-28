package com.map.friends.friend_map.mapper;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE) // bỏ qua các giá trị null
public interface UserMapper   {


    UserDto toDto(User entity);

    User toEntity(UserDto dto);

    User responseToEntity(UserResponse response);

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

    @Mapping(target = "friend", ignore = true)
    @Mapping(target = "relationshipRole", ignore = true)
    UserSearchResponse toSearchResponse(User entity, Long currentUserId);

}
