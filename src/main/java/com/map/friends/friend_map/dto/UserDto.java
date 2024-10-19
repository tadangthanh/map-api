package com.map.friends.friend_map.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends BaseDto<Long>{
    private String facebookId;
    private String name;
    private String email;
    private boolean isLocationSharing;
    private Integer batteryLevel;
    private Double latitude;
    private Double longitude;

}
