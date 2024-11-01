package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.service.INotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final INotificationService notificationService;

    @GetMapping
    public ResponseData<?> getNotifications(@Min (0)@RequestParam(defaultValue = "0") int page,@Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", notificationService.getNotifications(page, size));
    }
    @PutMapping("/mark-as-read/all")
    public ResponseData<?> markAsReadAll() {
        notificationService.markAsReadAll();
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    @DeleteMapping("/delete/all")
    public ResponseData<?> deleteAllNotification() {
        notificationService.deleteAllNotification();
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    @PatchMapping("/{id}/mark-as-read")
    public ResponseData<?> markAsRead(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", notificationService.markAsRead(id));
    }
    @GetMapping("/count-unread")
    public ResponseData<?> countUnreadNotification() {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", notificationService.countUnreadNotification());
    }
}
