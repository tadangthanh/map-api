package com.map.friends.friend_map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMove implements Serializable {
    private String googleId;
    private String name;
    private String email;
    private boolean isLocationSharing;
    private Integer batteryLevel;
    private Double latitude;
    private Double longitude;
    private String avatarUrl;
}
