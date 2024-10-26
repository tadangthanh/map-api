package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.FriendShipStatus;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.mapper.UserMapper;
import com.map.friends.friend_map.repository.FriendShipRepository;
import com.map.friends.friend_map.repository.UserHasFriendRepository;
import com.map.friends.friend_map.service.IUserMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMappingService implements IUserMapping {
    private final UserMapper userMapper;
    private final FriendShipRepository friendShipRepository;
    @Override
    public UserDto toDto(User user) {
        return  userMapper.toDto(user);
    }

    @Override
    public User toEntity(UserDto userDto) {
        return userMapper.toEntity(userDto);
    }

    @Override
    public UserSearchResponse toSearchResponse(User entity, Long currentUserId) {
        UserSearchResponse response = userMapper.toSearchResponse(entity, currentUserId);
        FriendShipStatus friendShipStatus = friendShipRepository.findByAuthorAndTarget(currentUserId, entity.getId()).orElse(FriendShipStatus.NONE);
        boolean isFriend = friendShipStatus != FriendShipStatus.PENDING && friendShipStatus != FriendShipStatus.PENDING_YOU_ACCEPT;
        response.setFriend(isFriend);
        response.setFriendShipStatus(friendShipStatus);
        return response;
    }

    @Override
    public User responseToEntity(UserResponse response) {
        return userMapper.responseToEntity(response);
    }

    @Override
    public UserResponse toResponse(User entity) {
        return userMapper.toResponse(entity);
    }

    @Override
    public void updateEntityFromDto(UserRequestDto dto, User entity) {
        userMapper.updateEntityFromDto(dto, entity);
    }

    @Override
    public void updateEntityFromDto(UserDto dto, User entity) {
        userMapper.updateEntityFromDto(dto, entity);
    }
}
