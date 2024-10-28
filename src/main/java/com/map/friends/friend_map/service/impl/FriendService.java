package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.entity.UserHasFriend;
import com.map.friends.friend_map.repository.UserHasFriendRepository;
import com.map.friends.friend_map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserHasFriendRepository userHasFriendRepository;
    private final UserRepository userRepository;


    @Cacheable(value = "friends", key = "#googleId")
    public List<User> getFriends(String googleId) {
        User user = userRepository.findByGoogleId(googleId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<UserHasFriend> userHasFriends = userHasFriendRepository.getFriends(user.getId());
        List<User> users = new ArrayList<>();
        for (UserHasFriend userHasFriend : userHasFriends) {
            if (Objects.equals(userHasFriend.getUserA().getId(), user.getId())) {
                users.add(userHasFriend.getUserB());
            } else {
                users.add(userHasFriend.getUserA());
            }
        }
        return users;
    }
}
