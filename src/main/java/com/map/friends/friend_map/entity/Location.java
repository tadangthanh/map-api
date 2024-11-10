package com.map.friends.friend_map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location") // vị trí
public class  Location extends BaseEntity<Long> {
    @NotNull
    private Double latitude; // vĩ độ
    @NotNull
    private Double longitude; // kinh độ
    private String name;
    private String description;
}
