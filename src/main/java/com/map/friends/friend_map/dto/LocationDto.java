package com.map.friends.friend_map.dto;

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
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 20, message = "Name must be between 1 and 20 characters")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    @Size(min = 1, max = 50, message = "Description must be between 1 and 50 characters")
    private String description;
    private String groupName;
    private Long groupId;
}
