package com.map.friends.friend_map.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FriendRequest {
    @NotBlank(message = "email is required")
    private String email;
}
