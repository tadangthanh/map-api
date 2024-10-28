package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.request.UserMove;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    UserResponse save(UserRequestDto userDto);

    UserDto update(UserDto userDto);

    UserSearchResponse findByEmail(String email);

    UserSearchResponse requestAddFriend(FriendRequest friendRequest);

    UserSearchResponse cancelAddFriend(FriendRequest friendRequest);

    UserSearchResponse acceptAddFriend(FriendRequest friendRequest);

    UserSearchResponse rejectAddFriend(FriendRequest friendRequest);

    PageResponse<?> getFriends(int pageNo, int pageSize);

    PageResponse<?> getPendingFriendRequests(int pageNo, int pageSize);
    void onMove(UserMove userDto);

    List<UserDto> getAllFriends();
}
