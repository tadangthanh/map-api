package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.request.UserMove;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.FriendShip;
import com.map.friends.friend_map.entity.Role;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.entity.UserHasFriend;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.FriendShipRepository;
import com.map.friends.friend_map.repository.RoleRepository;
import com.map.friends.friend_map.repository.UserHasFriendRepository;
import com.map.friends.friend_map.repository.UserRepository;
import com.map.friends.friend_map.service.IUserMapping;
import com.map.friends.friend_map.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {
    private final IUserMapping userMapping;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserHasFriendRepository userHasFriendRepository;
    private final FriendShipRepository friendShipRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public UserResponse save(UserRequestDto userDto) {
        // neu da co thi cap nhat
        if (userRepository.existsUserByEmail(userDto.getEmail()) && userRepository.existsUserByGoogleId(userDto.getGoogleId())) {
            User userExisting = userRepository.findByGoogleId(userDto.getGoogleId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            userExisting.setLocationSharing(true);
            userMapping.updateEntityFromDto(userDto, userExisting);
            userRepository.save(userExisting);
            return userMapping.toResponse(userExisting);
        }
        User entity = new User();
        entity.setLocationSharing(true);
        entity.setGoogleId(userDto.getGoogleId());
        entity.setName(userDto.getName());
        entity.setEmail(userDto.getEmail());
        entity = userRepository.saveAndFlush(entity);
        Role roleUser = roleRepository.findRoleByName("ROLE_USER");
        entity.setRole(roleUser);
        userRepository.save(entity);
        return userMapping.toResponse(entity);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User userExisting = userRepository.findByGoogleId(userDto.getGoogleId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapping.updateEntityFromDto(userDto, userExisting);
        userRepository.save(userExisting);
        return userMapping.toDto(userExisting);
    }

    @Override
    public UserSearchResponse findByEmail(String email) {
        if (!email.contains("@")) {
            email = email.concat("@gmail.com");
        }
        User currentUser = getCurrentUser();
        if (currentUser.getEmail().equals(email)) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapping.toSearchResponse(user, getCurrentUser().getId());
    }

    // gui loi moi ket ban
    @Override
    public UserSearchResponse requestAddFriend(FriendRequest friendRequest) {
        User user = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // kiem tra xem da la ban chua
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(user.getId(), friend.getId());
        if (isFriend) {
            return userMapping.toSearchResponse(friend, user.getId());
        }
        // kiem tra xem da gui loi moi cho nguoi nay hay chua, neu roi thi khong gui nua
        FriendShip friendShip = friendShipRepository.findFriendShipBetweenUsers(user.getId(), friend.getId()).orElse(null);
        if (friendShip != null) {
            return userMapping.toSearchResponse(friend, user.getId());
        }

        // neu chua thi gui loi moi ket ban
        friendShip = new FriendShip();
        friendShip.setAuthor(user);
        friendShip.setTarget(friend);
        // author la nguoi gui va se co trang thai la pending
        friendShipRepository.saveAndFlush(friendShip);
        return userMapping.toSearchResponse(friend, user.getId());
        // them logic gui thong bao cho nguoi dung

    }

    //thu hoi yeu cau ket ban
    @Override
    public UserSearchResponse cancelAddFriend(FriendRequest friendRequest) {
        User user = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(user.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, user.getId());
        }
        // kiem tra xem co gui loi moi nao toi user nay chua
        FriendShip friendShip = friendShipRepository.findByAuthorAndTarget(user.getId(), friend.getId()).orElse(null);
        // neu nguoi dung da gui loi moi ket ban thi huy
        if (friendShip != null) {
            // xoa loi moi ket ban
            friendShipRepository.delete(friendShip);
            return userMapping.toSearchResponse(friend, user.getId());
        }
        return userMapping.toSearchResponse(friend, user.getId());
    }

    @Override
    public UserSearchResponse acceptAddFriend(FriendRequest friendRequest) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        // kiem tra xem co loi moi ket ban tu nguoi nay hay chua
        FriendShip friendShip = friendShipRepository.findByAuthorAndTarget(friend.getId(), currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if (friendShip == null) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        friendShipRepository.delete(friendShip);
        // them ban
        UserHasFriend userHasFriend = new UserHasFriend();
        userHasFriend.setUserA(currentUser);
        userHasFriend.setUserB(friend);
        userHasFriendRepository.saveAndFlush(userHasFriend);
        return userMapping.toSearchResponse(friend, currentUser.getId());
    }

    @Override
    public UserSearchResponse rejectAddFriend(FriendRequest friendRequest) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        // kiem tra xem co nhan loi moi ket ban tu nguoi nay hay chua
        FriendShip friendShip = friendShipRepository.findByAuthorAndTarget(friend.getId(), currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if (friendShip == null) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        friendShipRepository.delete(friendShip);
        return userMapping.toSearchResponse(friend, currentUser.getId());
    }

    @Override
    public PageResponse<?> getFriends(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserHasFriend> userHasFriends = userHasFriendRepository.getFriends(user.getId(), pageable);
        List<User> users = new ArrayList<>();
        // lay danh sach ban be cua user
        //neu userA la user hien tai thi userB la ban be va nguoc lai
        for (UserHasFriend userHasFriend : userHasFriends) {
            if (Objects.equals(userHasFriend.getUserA().getId(), user.getId())) {
                users.add(userHasFriend.getUserB());
            } else {
                users.add(userHasFriend.getUserA());
            }
        }
        List<UserSearchResponse> userSearchResponses = new ArrayList<>();
        for (User u : users) {
            userSearchResponses.add(userMapping.toSearchResponse(u, user.getId()));
        }
        userSearchResponses.sort(Comparator.comparing(UserSearchResponse::getName));
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(userHasFriends.getTotalPages())
                .totalItems(userHasFriends.getTotalElements())
                .hasNext(userHasFriends.hasNext())
                .items(userSearchResponses).build();
    }

    @Override
    public PageResponse<?> getPendingFriendRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = getCurrentUser();
        // lấy danh sách lời mời kết bạn chờ xác nhận của user hiện tại đang đăng nhập
        // tức là user hiện tại là target
        Page<FriendShip> friendShips = friendShipRepository.findByTarget(user.getId(), pageable);
        List<User> users = friendShips.stream().map(FriendShip::getAuthor).toList();
        List<UserSearchResponse> userSearchResponses = new ArrayList<>();
        for (User u : users) {
            userSearchResponses.add(userMapping.toSearchResponse(u, user.getId()));
        }
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(friendShips.getTotalPages())
                .totalItems(friendShips.getTotalElements())
                .hasNext(friendShips.hasNext())
                .items(userSearchResponses).build();

    }

    @Override
    public void onMove(UserMove userDto) {
        System.out.println("receiverPrivateMessage: " + userDto.getEmail());
        simpMessagingTemplate.convertAndSendToUser(userDto.getEmail(), "/private/friend-location", userDto);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


}
