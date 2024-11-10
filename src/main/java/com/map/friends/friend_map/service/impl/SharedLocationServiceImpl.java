package com.map.friends.friend_map.service.impl;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.dto.request.SharedLocationRequest;
import com.map.friends.friend_map.dto.response.PageResponse;
import com.map.friends.friend_map.entity.Location;
import com.map.friends.friend_map.entity.NotificationType;
import com.map.friends.friend_map.entity.SharedLocation;
import com.map.friends.friend_map.entity.User;
import com.map.friends.friend_map.exception.DuplicateGroupLocationException;
import com.map.friends.friend_map.exception.ResourceNotFoundException;
import com.map.friends.friend_map.exception.UnAuthorizeException;
import com.map.friends.friend_map.repository.LocationRepo;
import com.map.friends.friend_map.repository.SharedLocationRepo;
import com.map.friends.friend_map.repository.UserRepo;
import com.map.friends.friend_map.service.INotificationService;
import com.map.friends.friend_map.service.ISharedLocationMapper;
import com.map.friends.friend_map.service.ISharedLocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SharedLocationServiceImpl implements ISharedLocationService {
    private final ISharedLocationMapper sharedLocationMapper;
    private final LocationRepo locationRepo;
    private final UserRepo userRepo;
    private final SharedLocationRepo sharedLocationRepo;
    private final INotificationService notificationService;

    @Override
    public SharedLocationDto share(SharedLocationRequest sharedLocationRequest) {
        SharedLocation sharedLocation = new SharedLocation();
        sharedLocation.setNote(sharedLocationRequest.getNote());
        User currentUser = getCurrentUser();
        // save location
        Location location = createLocation(sharedLocationRequest.getLatitude(), sharedLocationRequest.getLongitude());
        sharedLocation.setLocation(location);
        sharedLocation.setSender(currentUser);
        sharedLocationRequest.getReceiverIds().forEach(receiverId -> {
            User receiver = getUser(receiverId);
            sharedLocation.setReceiver(receiver);
            // validate location
            validateLocation(sharedLocation);
            notificationService.createNotificationToUser(receiver.getGoogleId(), currentUser.getName(), "Đã gửi 1 địa điểm cho bạn", NotificationType.SHARE_LOCATION);
            sharedLocationRepo.saveAndFlush(sharedLocation);
        });
        return sharedLocationMapper.toDto(sharedLocationRepo.saveAndFlush(sharedLocation));
    }

    private User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Location createLocation(Double latitude, Double longitude) {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return locationRepo.saveAndFlush(location);
    }

    @Override
    public void delete(Long id) {
        SharedLocation sharedLocation = sharedLocationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shared location not found"));
        User currentUser = getCurrentUser();
        validateUserDelete(currentUser, sharedLocation);
        // neu nguoi xoa la nguoi chia se thi xoa luon dia diem, con khong thi chi xoa chia se
        if (sharedLocation.getSender().getId().equals(currentUser.getId())) {
            deleteSharedLocationBySender(sharedLocation);
        } else {
            sharedLocationRepo.delete(sharedLocation);
        }
    }

    private void deleteSharedLocationBySender(SharedLocation sharedLocation) {
        sharedLocationRepo.delete(sharedLocation);
        locationRepo.deleteById(sharedLocation.getLocation().getId());
    }

    private void validateUserDelete(User currentUser, SharedLocation sharedLocation) {
        if (!sharedLocation.getSender().getId().equals(currentUser.getId()) && !sharedLocation.getReceiver().getId().equals(currentUser.getId())) {
            throw new UnAuthorizeException("You are not authorized to delete this shared location");
        }
    }

    private void validateLocation(SharedLocation sharedLocation) {
        Location location = sharedLocation.getLocation();
        boolean existsLocationSharedByMe = sharedLocationRepo.existsLocationSharedByMe(sharedLocation.getReceiver().getId(), location.getLatitude(), location.getLongitude());
        if (existsLocationSharedByMe) {
            throw new DuplicateGroupLocationException("Đã chia sẻ vị trí này rồi");
        }
    }


    //lay danh sach vi tri da chia se voi toi
    @Override
    public PageResponse<?> fetchLocationsSharedWithMe(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = getCurrentUser();
        Page<SharedLocation> sharedLocationPage = sharedLocationRepo.findAllByReceiverId(user.getId(), pageable);
        List<SharedLocationDto> sharedLocationDtoList = sharedLocationMapper.toDtoList(sharedLocationPage.getContent());
        return PageResponse.builder()
                .items(sharedLocationDtoList)
                .totalItems(sharedLocationPage.getTotalElements()).
                totalPage(sharedLocationPage.getTotalPages())
                .hasNext(sharedLocationPage.hasNext())
                .pageNo(page).pageSize(size).build();
    }

    // lay danh sach cac dia diem toi da chia se
    @Override
    public PageResponse<?> fetchSharedLocationsByMe(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = getCurrentUser();
        Page<SharedLocation> sharedLocationPage = sharedLocationRepo.findAllBySenderId(user.getId(), pageable);
        List<SharedLocationDto> sharedLocationDtoList = sharedLocationMapper.toDtoList(sharedLocationPage.getContent());
        return PageResponse.builder()
                .items(sharedLocationDtoList)
                .totalItems(sharedLocationPage.getTotalElements()).
                totalPage(sharedLocationPage.getTotalPages())
                .hasNext(sharedLocationPage.hasNext())
                .pageNo(page).pageSize(size).build();
    }

    private User getCurrentUser() {
        return userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UnAuthorizeException("User not found"));
    }
}
