package com.map.friends.friend_map.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    @NotBlank(message = "Password is required")
    private String password;
    private String email;
    @NotNull(message = "Platform is required")
//    private Platform platform;
    private String deviceToken; // gui thong bao cho thiet bi bang device token
    private String version; // phien ban cua ung dung
}
