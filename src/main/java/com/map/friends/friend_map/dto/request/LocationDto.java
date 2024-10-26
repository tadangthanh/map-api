package com.map.friends.friend_map.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class LocationDto implements Serializable {
    private  Double latitude; // vĩ độ
    private  Double longitude; // kinh độ
    private Long userId;
}
