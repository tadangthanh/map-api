package com.map.friends.friend_map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.map.friends.friend_map.validator.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto extends BaseDto<Long> {
    @NotNull(message = "Latitude cannot be empty")
    private Double latitude;
    @NotNull(message = "Longitude cannot be empty")
    private Double longitude;
    private String name;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String groupName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long groupId;
}
