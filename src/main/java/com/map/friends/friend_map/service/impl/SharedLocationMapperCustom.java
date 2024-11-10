package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.LocationDto;
import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.entity.SharedLocation;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.mapper.SharedLocationMapper;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.ILocationMapper;
import com.map.friends.friend_map.service.ISharedLocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedLocationMapperCustom implements ISharedLocationMapper {
    private final UserRepo userRepo;
    private final SharedLocationMapper sharedLocationMapper;
    private final ILocationMapper locationMapper;

    @Override
    public SharedLocation toEntity(SharedLocationDto sharedLocationDto) {
        SharedLocation sharedLocation = sharedLocationMapper.toEntity(sharedLocationDto);
        User sender = userRepo.findById(sharedLocationDto.getSenderId()).orElseThrow(() -> new ResourceNotFoundException("Sender shared location not found"));
        sharedLocation.setSender(sender);
        User receiver = userRepo.findById(sharedLocationDto.getReceiverId()).orElseThrow(() -> new ResourceNotFoundException("Receiver shared location not found"));
        sharedLocation.setReceiver(receiver);
        return sharedLocation;
    }

    @Override
    public SharedLocationDto toDto(SharedLocation sharedLocation) {
        return sharedLocationMapper.toDto(sharedLocation);
    }

    @Override
    public List<SharedLocationDto> toDtoList(List<SharedLocation> sharedLocations) {
        return sharedLocationMapper.toDtoList(sharedLocations);
    }
}
