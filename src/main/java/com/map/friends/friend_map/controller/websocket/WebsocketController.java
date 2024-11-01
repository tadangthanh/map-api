package com.map.friends.friend_map.controller.websocket;

import com.map.friends.friend_map.dto.UserDto;
import com.map.friends.friend_map.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
    private final IUserService userService;
    @MessageMapping("/on-move") // client send message to this endpoint ex: client.send(destination: '/app/on-move', body: 'Hello');
    public void receiverPrivateMessage(@Payload UserDto userMove) {
        userService.onMove(userMove);
    }

}
