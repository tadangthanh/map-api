package com.map.friends.friend_map.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseDto <T> implements Serializable {
    private T id;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
