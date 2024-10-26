package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.dto.response.TokenResponse;
import com.map.friends.friend_map.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/google")
    public ResponseData<TokenResponse> loginWithGoogle(@RequestHeader("X-ID-TOKEN") String idToken) {
        TokenResponse tokenResponse = authenticationService.authenticateWithGoogle(idToken);
        return new ResponseData<>(HttpStatus.OK.value(), "Google login success", tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseData<TokenResponse> refreshToken(@RequestHeader("X-Refresh-Token")String rfToken) {
        TokenResponse tokenResponse = authenticationService.refreshToken(rfToken.substring(7));
        return new ResponseData<>(HttpStatus.OK.value(), "Refresh token success", tokenResponse);
    }

}
