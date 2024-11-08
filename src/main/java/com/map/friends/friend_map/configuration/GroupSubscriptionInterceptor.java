package com.map.friends.friend_map.configuration;

import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.entity.UserHasGroup;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.exception.UnAuthorizeException;
import com.map.friends.friend_map.repository.UserHasGroupRepo;
import com.map.friends.friend_map.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class GroupSubscriptionInterceptor implements ChannelInterceptor {
    private final UserHasGroupRepo userHasGroupRepo;
    private final UserRepo userRepo;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            // Kiểm tra nếu người dùng đang cố gắng đăng ký vào một chủ đề nhóm
            if (destination != null && destination.startsWith("/topic/group-location/")) {
                // Lấy groupId từ đường dẫn
                String groupIdStr = destination.split("/")[3];
                Long groupId = Long.valueOf(groupIdStr);
                // Lấy thông tin người dùng từ phiên WebSocket
                String userEmail = Objects.requireNonNull(accessor.getUser()).getName();
                // Kiểm tra xem người dùng có thuộc nhóm hay không (tùy theo logic của bạn)
                if (!isUserMemberOfGroup(userEmail, groupId)) {
                    throw new IllegalArgumentException("User does not have permission to subscribe to this group");
                }
            }
        }
        return message;
    }

    private boolean isUserMemberOfGroup(String userEmail, Long groupId) {
        // Kiểm tra xem người dùng có thuộc nhóm này không.
        // Bạn có thể sử dụng dịch vụ để kiểm tra trong cơ sở dữ liệu.
        User user = userRepo.findByEmail(userEmail).orElseThrow(() -> new UnAuthorizeException(" User not found"));
        return userHasGroupRepo.existsByUserIdAndGroupId(user.getId(), groupId);
    }
}