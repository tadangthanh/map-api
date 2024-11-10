package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.entity.GroupHasLocation;
import com.map.friends.friend_map.entity.Location;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.mapper.LocationMapper;
import com.map.friends.friend_map.repository.GroupHasLocationRepo;
import com.map.friends.friend_map.service.ILocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationMapperCustom implements ILocationMapper {
    private final LocationMapper locationMapper;
    private final GroupHasLocationRepo groupHasLocationRepo;

    @Override
    public LocationDto toDto(Location location) {
        LocationDto locationDto = locationMapper.toDto(location);
        GroupHasLocation groupHasLocation = groupHasLocationRepo.findByLocationId(location.getId()).orElse(null);
        if (groupHasLocation == null) {
            return locationDto;
        }
        locationDto.setGroupName(groupHasLocation.getGroup().getName());
        locationDto.setGroupId(groupHasLocation.getGroup().getId());
        return locationDto;
    }

    @Override
    public Location toEntity(LocationDto locationDto) {
        Location location = locationMapper.toEntity(locationDto);
        return location;
    }

    @Override
    public List<LocationDto> toDtoList(List<Location> locations) {
        List<LocationDto> result= new ArrayList<>();
        for (Location location : locations) {
            result.add(toDto(location));
        }
        return result;
    }
}
