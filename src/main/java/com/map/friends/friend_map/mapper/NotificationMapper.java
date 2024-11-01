package com.map.friends.friend_map.mapper;
import com.map.friends.friend_map.dto.NotificationDto;
import com.map.friends.friend_map.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface NotificationMapper {
    @Mapping(target = "recipientGoogleId", source = "recipient.googleId")
    @Mapping(target = "senderGoogleId", source = "sender.googleId")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "senderAvatarUrl", source = "sender.avatarUrl")
    @Mapping(target = "senderName", source = "sender.name")
    NotificationDto toDto(Notification entity);

    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "id", ignore = true)
    Notification toEntity(NotificationDto dto);

    @Mapping(target = "id", source = "id")
    List<NotificationDto> toDtoList(List<Notification> entities);
}
