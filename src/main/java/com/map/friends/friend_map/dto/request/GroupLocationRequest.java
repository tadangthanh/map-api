package com.map.friends.friend_map.dto.request;

import com.map.friends.friend_map.dto.BaseDto;
import com.map.friends.friend_map.dto.LocationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupLocationRequest extends BaseDto<Long> {
    @NotEmpty(message = "Group cannot be empty")
    private List<Long> groupIds;
    @Valid
    private LocationDto location;
}
