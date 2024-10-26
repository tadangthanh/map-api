package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.request.FriendRequest;
import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.dto.response.UserSearchResponse;
import com.map.friends.friend_map.service.IUserService;
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
    @GetMapping("/friend/pending/accept")
    public ResponseData<?> getPendingFriendRequests(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseData<>(HttpStatus.OK.value(), "Friend requests found successfully", this.userService.getFriendPendingAccept(pageNo, pageSize));
    }

}
