package com.map.friends.friend_map.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.map.friends.friend_map.dto.response.TokenResponse;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.UserRepository;
import com.map.friends.friend_map.service.impl.AuthenticationService;
import com.map.friends.friend_map.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoogleAuthControllerTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoogleIdToken googleIdToken;
    @Mock
    private JwtService jwtService;
    @Mock
    private GoogleIdToken.Payload payload;
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void authenticateWithGoogle_ExistingUser_ShouldReturnTokenResponse() throws GeneralSecurityException, IOException {
        // Giả lập GoogleIdToken
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);

        // Setup dữ liệu payload
        when(payload.getEmail()).thenReturn("test@example.com");
        when(payload.get("name")).thenReturn("Test User");
        when(payload.get("picture")).thenReturn("http://example.com/avatar.jpg");

        // Giả lập tìm thấy người dùng
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("testAccessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("testRefreshToken");

        // Thực hiện kiểm thử
        TokenResponse response = authenticationService.authenticateWithGoogle("validIdToken");

        // Xác minh kết quả
        assertNotNull(response);
        assertEquals("testAccessToken", response.getAccessToken());
        assertEquals("testRefreshToken", response.getRefreshToken());
        assertEquals(user.getId(), response.getUserId());
    }

    @Test
    public void authenticateWithGoogle_InvalidToken_ShouldThrowException() throws GeneralSecurityException, IOException {
        // Giả lập trường hợp không xác minh được token
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            authenticationService.authenticateWithGoogle("invalidIdToken");
        });
    }
}
