package com.map.friends.friend_map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
public class FriendMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendMapApplication.class, args);
	}

}
