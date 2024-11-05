package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.request.UserRequestDto;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.dto.response.UserResponse;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.entity.*;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.FriendShipRepo;
import com.map.friends.friend_map.repository.RoleRepo;
import com.map.friends.friend_map.repository.UserHasFriendRepo;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.INotificationService;
import com.map.friends.friend_map.service.IUserMapper;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {
    private final IUserMapper userMapping;
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final JedisPool jedisPool;
    private final UserHasFriendRepo userHasFriendRepository;
    private final FriendShipRepo friendShipRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FriendService friendService;
    private final INotificationService notificationService;

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
        User currentUser = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // kiem tra xem da la ban chua
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        if (isFriend) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }
        // kiem tra xem da gui loi moi cho nguoi nay hay chua, neu roi thi khong gui nua
        FriendShip friendShip = friendShipRepository.findFriendShipBetweenUsers(currentUser.getId(), friend.getId()).orElse(null);
        if (friendShip != null) {
            return userMapping.toSearchResponse(friend, currentUser.getId());
        }

        // neu chua thi gui loi moi ket ban
        friendShip = new FriendShip();
        friendShip.setAuthor(currentUser);
        friendShip.setTarget(friend);
        // author la nguoi gui va se co trang thai la pending
        friendShipRepository.saveAndFlush(friendShip);
        // thong bao cho nguoi nhan
        notificationService.createNotification(currentUser.getGoogleId(), friend.getGoogleId(), null, "Yêu cầu kết bạn", currentUser.getName() + " đã gửi lời mời kết bạn", NotificationType.FRIEND_REQUEST);
        return userMapping.toSearchResponse(friend, currentUser.getId());
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
            // xoa thong bao ve loi moi ket ban do
            notificationService.deleteBySenderRecipientAndType(user.getId(), friend.getId(), NotificationType.FRIEND_REQUEST);
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
        notificationService.deleteBySenderRecipientAndType(friend.getId(), currentUser.getId(), NotificationType.FRIEND_REQUEST);
        notificationService.createNotification(currentUser.getGoogleId(), friend.getGoogleId(), null, currentUser.getName(), currentUser.getName() + " đã chấp nhận lời mời kết bạn", NotificationType.ACCEPT_FRIEND);
        // xoa cache friends
        jedisPool.getResource().del("friends:" + currentUser.getGoogleId());
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
        notificationService.deleteBySenderRecipientAndType(friend.getId(), currentUser.getId(), NotificationType.FRIEND_REQUEST);
        return userMapping.toSearchResponse(friend, currentUser.getId());
    }

    @Override
    public void unFriend(FriendRequest friendRequest) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findByEmail(friendRequest.getEmail().trim()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isFriend = userHasFriendRepository.isFriendBetweenUserAAndUserB(currentUser.getId(), friend.getId());
        // neu la ban thi moi xoa
        if (isFriend) {
            UserHasFriend userHasFriend = userHasFriendRepository.findFriendShipBetweenUsers(currentUser.getId(), friend.getId()).orElseThrow(() -> new ResourceNotFoundException("2 users are not friends"));
            userHasFriendRepository.delete(userHasFriend);
        }
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
    public void onMove(UserDto userDto) {
        System.out.println("receiverPrivateMessage: " + userDto.getEmail());
        List<User> users = friendService.getFriends(userDto.getGoogleId());
        saveLastPositionToRedis(userDto.getEmail(), userDto.getLatitude(), userDto.getLongitude());
        for (User user : users) {
            simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/private/friend-location", userDto);
        }
    }


    private void saveLastPositionToRedis(String email, double latitude, double longitude) {
        try (Jedis jedis = jedisPool.getResource()) {
            String userKey = "user:location:" + email;
            Map<String, String> locationData = new HashMap<>();
            locationData.put("latitude", String.valueOf(latitude));
            locationData.put("longitude", String.valueOf(longitude));

            // Lưu thông tin vị trí vào Redis
            jedis.hmset(userKey, locationData);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu vị trí vào Redis cho người dùng: " + email);
            e.printStackTrace();
        }
    }


    @Override
    public List<UserDto> getAllFriends() {
        List<User> friends = friendService.getFriends(getCurrentUser().getGoogleId());
        List<UserDto> userDtos = new ArrayList<>();
        for (User u : friends) {
            userDtos.add(userMapping.toDto(u));
        }
        return userDtos;
    }

    private void sendLastLocationToFriendsByUser(User user) {
        List<User> friends = friendService.getFriends(user.getGoogleId());
        saveLastPositionToRedis(user.getEmail(), user.getLatitude(), user.getLongitude());
        UserDto userDto = userMapping.toDto(user);
        for (User u : friends) {
            simpMessagingTemplate.convertAndSendToUser(u.getEmail(), "/private/friend-location", userDto);
        }
    }

    @Override
    public void updateLocationOffline(UserDto userDto) {
        User user = userRepository.findByGoogleId(userDto.getGoogleId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLatitude(userDto.getLatitude());
        user.setLongitude(userDto.getLongitude());
        user.setLastTimeOnline(LocalTime.now());
        sendLastLocationToFriendsByUser(user);
        userRepository.save(user);
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
