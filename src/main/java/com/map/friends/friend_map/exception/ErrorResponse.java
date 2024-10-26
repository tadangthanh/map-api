package com.map.friends.friend_map.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
public class ErrorResponse implements Serializable {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
