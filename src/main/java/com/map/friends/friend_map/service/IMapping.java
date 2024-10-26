package com.map.friends.friend_map.service;

public interface IMapping<Entity,Dto> {
    Dto toDto(Entity entity);
    Entity toEntity(Dto dto);
}
