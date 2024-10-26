package com.map.friends.friend_map.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.map.friends.friend_map.dto.request.SignInRequest;
import com.map.friends.friend_map.dto.response.TokenResponse;
import com.map.friends.friend_map.entity.Role;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.exception.UnAuthorizeException;
import com.map.friends.friend_map.repository.RoleRepository;
import com.map.friends.friend_map.repository.UserRepository;
import com.map.friends.friend_map.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CLIENT_ID = "239930631001-iq3pg1qda24qhim5maj38bqfr3pbf6ho.apps.googleusercontent.com";
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private final JwtService jwtService;
    private final IUserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Username or password is incorrect"));
        String accessToken = jwtService.generateToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh")
                .userId(user.getId())
                .build();
    }

    public TokenResponse authenticateWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                // Tìm user trong cơ sở dữ liệu
                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            // Tạo user mới nếu chưa tồn tại
                            Role roleUser = roleRepository.findRoleByName("ROLE_USER");
                            User newUser = new User();
                            newUser.setEmail(email);
                            newUser.setName((String) payload.get("name"));
                            newUser.setAvatarUrl((String) payload.get("picture"));
                            newUser.setGoogleId(payload.getSubject());
                            newUser.setLocationSharing(true);
                            newUser.setBatteryLevel(100);
                            userRepository.save(newUser);
                            newUser.setRole(roleUser);
                            userRepository.save(newUser);
                            return newUser;
                        });
                // Tạo access token
                String accessToken = jwtService.generateToken(user);
                // Tạo refresh token (ví dụ: sử dụng UUID, có thể thay bằng cách tạo khác phù hợp)
                String refreshToken = jwtService.generateRefreshToken(user);
                return TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userId(user.getId())
                        .build();
            } else {
                throw new ResourceNotFoundException("Invalid token");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new UnAuthorizeException("Invalid token");
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtService.tokenIsValid(refreshToken)) {
            throw new UnAuthorizeException("Invalid refresh token");
        }
        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String accessToken = jwtService.generateToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
