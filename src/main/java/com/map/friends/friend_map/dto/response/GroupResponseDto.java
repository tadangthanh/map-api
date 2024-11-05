package com.map.friends.friend_map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.map.friends.friend_map.dto.BaseDto;
import com.map.friends.friend_map.entity.UserGroupStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GroupResponseDto extends BaseDto<Long> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private int totalMembers;
    private String role;
    private List<String> permissions;
    @Enumerated(EnumType.STRING)
    private UserGroupStatus status;
}
