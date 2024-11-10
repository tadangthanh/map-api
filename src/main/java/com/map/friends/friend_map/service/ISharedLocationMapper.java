package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.entity.SharedLocation;

import java.util.List;

public interface ISharedLocationMapper extends IMapping<SharedLocation, SharedLocationDto> {
    SharedLocation toEntity(SharedLocationDto sharedLocationDto);

    SharedLocationDto toDto(SharedLocation sharedLocation);

    List<SharedLocationDto> toDtoList(List<SharedLocation> sharedLocations);
}
