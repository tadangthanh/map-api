package com.map.friends.friend_map.mapper;

import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface LocationMapper {
    @Mapping(target = "id",source = "id")
    LocationDto toDto(Location entity);

    @Mapping(target = "id",source = "id")
    Location toEntity(LocationDto dto);

    List<LocationDto> toDtoList(List<Location> entities);
}
