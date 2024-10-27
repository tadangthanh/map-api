package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.FriendShip;
import com.map.friends.friend_map.entity.RelationshipRole;
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
    private final UserHasFriendRepository userHasFriendRepository;

    @Override
    public UserDto toDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public User toEntity(UserDto userDto) {
        return userMapper.toEntity(userDto);
    }

    @Override
    public UserSearchResponse toSearchResponse(User entity, Long currentUserId) {
        UserSearchResponse response = userMapper.toSearchResponse(entity, currentUserId);
        FriendShip friendShip = friendShipRepository.findFriendShipBetweenUsers(currentUserId, entity.getId()).orElse(null);
        if (friendShip != null) {
            // kiểm tra xem người dùng hiện tại là author hay target trong friendShip
            // để xác định quan hệ bạn bè giữa 2 người
            // nếu người được tìm kiếm là target thì tức là người nhận được lời mời kết bạn
            if (friendShip.getAuthor().getId().equals(entity.getId())) {
                response.setRelationshipRole(RelationshipRole.AUTHOR);
            } else if (friendShip.getTarget().getId().equals(entity.getId())) {
                response.setRelationshipRole(RelationshipRole.TARGET);
            }
        } else {
            response.setRelationshipRole(RelationshipRole.NONE);
        }
        // nếu là bạn bè thì đã bị xóa trong table friendShip
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(currentUserId, entity.getId());
        response.setFriend(isFriend);
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
