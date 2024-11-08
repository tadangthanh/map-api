package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.entity.Location;

import java.util.List;

public interface ILocationMapper extends  IMapping<Location, LocationDto> {
    List<LocationDto> toDtoList(List<Location> locations);
}
