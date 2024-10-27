package com.map.friends.friend_map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto <T> implements Serializable {
    private T id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createdBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updatedBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date updatedAt;
}
