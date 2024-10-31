package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.NotificationMessage;
import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.service.IUserService;
import com.map.friends.friend_map.service.impl.FirebaseMessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Valid
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserRest {
    private final IUserService userService;
    private final FirebaseMessagingService firebaseMessagingService;
    @GetMapping("/email/{email}")
    public ResponseData<?> getUserByEmail(@PathVariable String email) {
        UserSearchResponse response = this.userService.findByEmail(email.trim());
        return new ResponseData<>(HttpStatus.OK.value(), "User found successfully", response);
    }
    @PostMapping("/add")
    public ResponseData<?> requestAddFriendRequest(@RequestBody FriendRequest friendRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Friend request sent successfully", this.userService.requestAddFriend(friendRequest));
    }
    @DeleteMapping("/cancel")
    public ResponseData<?> cancelFriendRequest(@RequestBody FriendRequest friendRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friend request cancelled successfully", this.userService.cancelAddFriend(friendRequest));
    }
    @DeleteMapping("/reject")
    public ResponseData<?> rejectFriendRequest(@RequestBody FriendRequest friendRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friend request rejected successfully", this.userService.rejectAddFriend(friendRequest));
    }
    @PostMapping("/accept")
    public ResponseData<?> acceptFriendRequest(@RequestBody FriendRequest friendRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friend request accepted successfully", this.userService.acceptAddFriend(friendRequest));
    }

    @GetMapping("/friends/pending/accept")
    public ResponseData<?> getPendingFriendRequests(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friend requests found successfully", this.userService.getPendingFriendRequests(page, size));
    }
    @GetMapping("/friends")
    public ResponseData<?> getFriends(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friends found successfully", this.userService.getFriends(page, size));
    }

    @GetMapping("/friends/all")
    public ResponseData<?> getAllFriends() {
        return new ResponseData<>(HttpStatus.OK.value(), "Friends found successfully", this.userService.getAllFriends());
    }

    @PostMapping("/update/location/offline")
    public ResponseData<?> updateLocationOffline(@RequestBody UserDto userDto) {
        System.out.println("updateLocationOffline " + userDto.getEmail());
        this.userService.updateLocationOffline(userDto);
        return new ResponseData<>(HttpStatus.OK.value(), "Test successfully", null);
    }
    @PostMapping("/test")
    public ResponseData<?> test() {
       System.out.println("Test");
        return new ResponseData<>(HttpStatus.OK.value(), "Test successfully", null);
    }
    @DeleteMapping("/unfriend")
    public ResponseData<?> unFriend(@RequestBody FriendRequest friendRequest) {
        this.userService.unFriend(friendRequest);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Unfriend successfully", null);
    }
}
