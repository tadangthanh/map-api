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
//@Table(name = "message")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Message extends BaseEntity<Long> {
//    @ManyToOne
//    @JoinColumn(name = "sender_id", nullable = false)
//    private User sender; // Người gửi
//
//    @ManyToOne
//    @JoinColumn(name = "chat_room_id", nullable = false)
//    private ChatRoom chatRoom; // Phòng chat mà tin nhắn thuộc về
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String content; // Nội dung tin nhắn
//    @Column(nullable = false)
//    private LocalDateTime sentAt;
//    @Column
//    private LocalDateTime readAt; // Thời điểm tin nhắn được đọc (nếu có)
//    @Column(nullable = false)
//    private boolean isRead; // Tin nhắn đã đọc chưa
//}
