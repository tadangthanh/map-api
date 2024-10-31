package com.map.friends.friend_map.configuration;

import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final JedisPool jedisPool;
    private final UserRepo userRepository;
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String email = getEmailFromEvent(event);
        if (email == null) {
            System.out.println("Không tìm thấy email trong SessionDisconnectEvent");
            return;
        }






        Optional<Map<String, String>> locationData = getLocationDataFromRedis(email);
        locationData.ifPresent(data -> {
            updateUserLocation(email, data);
        });
        System.out.println("User disconnected: " + email);
        // Thực hiện các hành động cần thiết khi người dùng ngắt kết nối
    }

    private String getEmailFromEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        return (String) headerAccessor.getSessionAttributes().get("email");
    }

    private Optional<Map<String, String>> getLocationDataFromRedis(String email) {
        try (Jedis jedis = jedisPool.getResource()) {
            String userKey = "user:location:" + email;
            Map<String, String> locationData = jedis.hgetAll(userKey);
            if (locationData == null || locationData.isEmpty()) {
                System.out.println("Không tìm thấy thông tin vị trí cho người dùng: " + email);
                return Optional.empty();
            }
            return Optional.of(locationData);
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin từ Redis: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void updateUserLocation(String email, Map<String, String> locationData) {
        try {
            double latitude = Double.parseDouble(locationData.get("latitude"));
            double longitude = Double.parseDouble(locationData.get("longitude"));

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setLastTimeOnline(LocalTime.now());
            userRepository.saveAndFlush(user);
            String cacheKey = "friends::" + user.getGoogleId();
            deleteCache(cacheKey);
            System.out.println("Cập nhật vị trí người dùng thành công: " + email);
        } catch (NumberFormatException e) {
            System.out.println("Dữ liệu vị trí không hợp lệ cho người dùng: " + email);
        } catch (ResourceNotFoundException e) {
            System.out.println("Không tìm thấy người dùng với email: " + email);
        }
    }

    private void deleteCache(String cacheKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.del(cacheKey);
            if (result == 1) {
                System.out.println("Đã xóa dữ liệu cache với khóa: " + cacheKey);
            } else {
                System.out.println("Không tìm thấy dữ liệu cache với khóa: " + cacheKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
