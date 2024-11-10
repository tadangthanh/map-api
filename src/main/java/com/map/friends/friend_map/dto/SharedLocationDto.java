package com.map.friends.friend_map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharedLocationDto extends BaseDto<Long> {
    @NotNull(message = "latitude is required")
    private Double latitude;
    @NotNull(message = "longitude is required")
    private Double longitude;
    @NotNull(message = "receiver is required")
    private Long receiverId;
    private String receiverName;
    private Long senderId;
    private String senderName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(max = 255, message = "note cannot be more than 255 characters")
    private String note;
}
