package com.map.friends.friend_map.dto.response;

import com.map.friends.friend_map.dto.BaseDto;
import com.map.friends.friend_map.entity.RelationshipRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserSearchResponse extends BaseDto<Long> implements Serializable {
    private String googleId;
    private String name;
    private String email;
    private String avatarUrl;
    private boolean isFriend;
    private RelationshipRole relationshipRole;
}
