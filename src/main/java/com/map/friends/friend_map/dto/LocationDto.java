package com.map.friends.friend_map.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LocationDto extends BaseDto<Long>{
    private double latitude;
    private double longitude;
    private LocalDateTime timeStamp;
    private String address;
    private String name;
    private String googleId;
}
