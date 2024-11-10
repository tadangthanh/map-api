package com.map.friends.friend_map.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class GroupRequestDto implements Serializable {
    @Size(max = 20, min = 1, message = "name length must be less than 20")
    private String name;
    @Size(max = 255, min = 1, message = "description length must be less than 100")
    private String description;
    @NotEmpty(message = "userIds is required")
    // min is 1 and current user is added to group by default -> 2
    @Size(min = 1, max = 10, message = "userIds must have between 1 and 10 elements")
    private List<Long> userIds;

}
