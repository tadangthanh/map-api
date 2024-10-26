package com.map.friends.friend_map.controller.websocket;

import com.map.friends.friend_map.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @MessageMapping("/share-location") // client send message to this endpoint ex: client.send(destination: '/app/share-location', body: 'Hello');
//    public UserDto receiverPrivateMessage(@Payload UserDto userDto) {
//        System.out.println("receiverPrivateMessage: " + userDto.getId());
//        simpMessagingTemplate.convertAndSendToUser(userDto.getId().toString(),"/location", userDto);
////        simpMessagingTemplate.convertAndSend("/topic/location", userDto);
//        return userDto;
//    }

    @MessageMapping("/location")
    public void receiverPrivateMessage(@Payload UserDto userDto) {
        System.out.println("receiverPrivateMessage: " + userDto.getGoogleId());
        simpMessagingTemplate.convertAndSend("/topic/public", userDto);
    }

}
