package com.map.friends.friend_map.mapper;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.entity.SharedLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface SharedLocationMapper {
    @Mapping(target = "id", source = "id")
    SharedLocation toEntity(SharedLocationDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverName", source = "receiver.name")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.name")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    SharedLocationDto toDto(SharedLocation entity);

    List<SharedLocationDto> toDtoList(List<SharedLocation> sharedLocations);
}
