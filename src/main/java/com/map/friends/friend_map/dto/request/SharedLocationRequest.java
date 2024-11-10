package com.map.friends.friend_map.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SharedLocationRequest {
    @NotNull(message = "latitude is required")
    private Double latitude;
    @NotNull(message = "longitude is required")
    private Double longitude;
    @NotEmpty(message = "receiver is required")
    private List<Long> receiverIds;
    @Size(max = 255, message = "note cannot be more than 255 characters")
    private String note;
}
