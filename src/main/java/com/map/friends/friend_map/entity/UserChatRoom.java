//package com.map.friends.friend_map.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "user_chat_room")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class UserChatRoom extends BaseEntity<Long> {
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "chat_room_id")
//    private ChatRoom chatRoom;
//
//    @Column(nullable = false)
//    private LocalDateTime joinedAt;
//
//    private boolean isMuted;
//    private boolean isPinned;
//    @ManyToOne
//    @JoinColumn(name = "admin_id")
//    private User admin;
//
//    @Column(nullable = false)
//    private boolean isAdmin;
//
//
//}
