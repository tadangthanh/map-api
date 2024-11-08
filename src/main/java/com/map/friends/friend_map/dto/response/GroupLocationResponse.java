package com.map.friends.friend_map.dto.response;

import com.map.friends.friend_map.dto.BaseDto;
import com.map.friends.friend_map.dto.LocationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GroupLocationResponse extends BaseDto<Long> {
    private Long groupId;
    private String groupName;
    private List<LocationDto> locations;
}
