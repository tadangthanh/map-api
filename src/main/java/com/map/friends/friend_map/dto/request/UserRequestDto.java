package com.map.friends.friend_map.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserRequestDto implements Serializable {
    @NotBlank(message = "google id is required")
    private String googleId;
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "email is invalid")
    private String email;
    @NotBlank(message = "avatarUrl is required")
    private String avatarUrl;
}
