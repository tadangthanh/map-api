package com.map.friends.friend_map.dto.request;

import com.map.friends.friend_map.dto.BaseDto;
import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.validator.Create;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupLocationRequest extends BaseDto<Long> {
    @NotEmpty(message = "Group cannot be empty",groups = {Create.class})
    private List<Long> groupIds;
    @Valid
    @NotNull(message = "location is required", groups = {Create.class})
    private LocationDto location;
}
