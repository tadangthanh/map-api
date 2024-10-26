package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.User;


public interface IUserMapping extends IMapping<User, UserDto> {
    UserSearchResponse toSearchResponse(User entity, Long currentUserId);

    User responseToEntity(UserResponse response);

    UserResponse toResponse(User entity);

    void updateEntityFromDto(UserRequestDto dto, User entity);

    void updateEntityFromDto(UserDto dto, User entity);

}
