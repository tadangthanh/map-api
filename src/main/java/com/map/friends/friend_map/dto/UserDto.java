package com.map.friends.friend_map.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.map.friends.friend_map.validator.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto extends BaseDto<Long> {
    @NotBlank(message = "facebookId is required", groups = {Create.class})
    private String googleId;
    @NotBlank(message = "name is required", groups = {Create.class})
    private String name;
    @NotBlank(message = "email is required", groups = {Create.class})
    private String email;
    @NotNull(message = "isLocationSharing is required", groups = {Create.class})
    private boolean isLocationSharing;
    @NotNull(message = "batteryLevel is required", groups = {Create.class})
    private Integer batteryLevel;
    @NotNull(message = "latitude is required", groups = {Create.class})
    private Double latitude;
    @NotNull(message = "longitude is required", groups = {Create.class})
    private Double longitude;
    private Double speed;
    private Double distance;
    private String avatarUrl;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime lastTimeOnline;
}
