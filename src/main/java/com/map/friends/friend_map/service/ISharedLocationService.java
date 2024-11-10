package com.map.friends.friend_map.service;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.dto.request.SharedLocationRequest;
import com.map.friends.friend_map.dto.response.PageResponse;

public interface ISharedLocationService {
    SharedLocationDto share(SharedLocationRequest sharedLocationRequest);

    void delete(Long id);

    // lay danh sach cac dia diem da chia se voi toi
    PageResponse<?> fetchLocationsSharedWithMe(int page, int size);

    // lay danh sach cac dia diem toi da chia se
    PageResponse<?> fetchSharedLocationsByMe(int page, int size);
}
