package com.map.friends.friend_map.dto.response;

import com.map.friends.friend_map.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserResponse extends BaseDto<Long> implements Serializable {
    private String googleId;
    private String name;
    private String email;
    private String avatarUrl;
    private boolean isLocationSharing;
    private Integer batteryLevel;
    private double latitude;
    private double longitude;
}
