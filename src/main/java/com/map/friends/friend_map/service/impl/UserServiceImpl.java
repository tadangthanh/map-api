package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.map.friends.friend_map.entity.FriendShipStatus.PENDING;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {
    private final IUserMapping userMapping;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserHasFriendRepository userHasFriendRepository;
    private final FriendShipRepository friendShipRepository;

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
        if(currentUser.getEmail().equals(email)){
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
        boolean isFriend = userHasFriendRepository.isFriendRelationshipBetweenUserAAndUserB(user.getId(), friend.getId());
        if (isFriend) {
            return userMapping.toSearchResponse(friend, user.getId());
        }
        // kiem tra xem da gui loi moi cho nguoi nay hay chua, neu roi thi khong gui nua
        FriendShip friendShip = friendShipRepository.findFriendShipByAuthorAndStatusPending(user.getId()).orElse(null);
        if (friendShip != null) {
            return userMapping.toSearchResponse(friend, user.getId());
        }

        // neu chua thi gui loi moi ket ban
        friendShip = new FriendShip();
        friendShip.setAuthor(user);
        friendShip.setTarget(friend);
        // author la nguoi gui va se co trang thai la pending
        friendShip.setStatus(PENDING);
        friendShipRepository.saveAndFlush(friendShip);
        FriendShip friendShipForReceiver = new FriendShip();
        friendShipForReceiver.setTarget(user);
        friendShipForReceiver.setAuthor(friend);
        //target la nguoi nhan, se co trang thai la pending_you_accept
        friendShipForReceiver.setStatus(FriendShipStatus.PENDING_YOU_ACCEPT);
        friendShipRepository.saveAndFlush(friendShipForReceiver);
        return userMapping.toSearchResponse(friend, user.getId());

        // them logic gui thong bao cho nguoi dung

    }

    //huy gui loi moi ket ban
    @Override
    public UserSearchResponse cancelAddFriend(FriendRequest friendRequest) {
        User user = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendRelationshipBetweenUserAAndUserB(user.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, user.getId());
        }
        // kiem tra xem co loi moi ket ban tu nguoi nay hay chua
        FriendShip friendShip = friendShipRepository.findFriendShipByAuthorAndStatusPending(user.getId()).orElse(null);
        // neu nguoi dung da gui loi moi ket ban thi huy
        if (friendShip != null) {
            // xoa loi moi ket ban
            friendShipRepository.deleteFriendShipByAuthorOrTargetId(user.getId());
            return userMapping.toSearchResponse(friend, user.getId());
        }
        return userMapping.toSearchResponse(friend, user.getId());
    }

    @Override
    public UserSearchResponse acceptAddFriend(FriendRequest friendRequest) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendRelationshipBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        // kiem tra xem co loi moi ket ban tu nguoi nay hay chua
        FriendShip friendShip = friendShipRepository.findByAuthorAndTargetAndStatusPending(friend.getId(), currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if(friendShip == null){
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        friendShipRepository.deleteFriendShipByAuthorOrTargetId(currentUser.getId());
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
        boolean isFriend = userHasFriendRepository.isFriendRelationshipBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        // neu la ban thi k lam gi ca
        if (isFriend) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        // kiem tra xem co loi moi ket ban tu nguoi nay hay chua
        FriendShip friendShip = friendShipRepository.findByAuthorAndTargetAndStatusPendingYouAccept(currentUser.getId(),friend.getId()).orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if(friendShip == null){
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        friendShipRepository.deleteFriendShipByAuthorOrTargetId(currentUser.getId());
        return userMapping.toSearchResponse(friend, currentUser.getId());
    }

    @Override
    public PageResponse<?> getFriends(int page, int size) {
        int p = 0;
        if (page > 0) {
            p = page - 1;
        }
        User user = getCurrentUser();
        return null;
    }

    @Override
    public PageResponse<?> getFriendPendingAccept(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = getCurrentUser();
        Page<FriendShip> friendShips = friendShipRepository.findByAuthorAndStatus(user.getId(), FriendShipStatus.PENDING_YOU_ACCEPT,pageable);
        List<User> users = friendShips.stream().map(FriendShip::getTarget).toList();
        List<UserSearchResponse> userSearchResponses = new ArrayList<>();
        for (User u : users) {
            userSearchResponses.add(userMapping.toSearchResponse(u, user.getId()));
        }
        return PageResponse.builder().pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).totalItems(friendShips.getTotalPages()).items(userSearchResponses).build();

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
