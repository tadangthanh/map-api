package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.entity.SharedLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedLocationRepo extends JpaRepository<SharedLocation, Long> {
    @Query("select s from SharedLocation s where s.receiver.id = ?1")
    Page<SharedLocation> findAllByReceiverId(Long receiverId, Pageable pageable);

    @Query("select s from SharedLocation s where s.sender.id = ?1")
    Page<SharedLocation> findAllBySenderId(Long senderId, Pageable pageable);

    @Query("select count(s) from SharedLocation s where s.location.id = ?1")
    int countByLocationId(Long locationId);

    @Query("select count(s) > 0 from SharedLocation s where s.receiver.id = ?1 and s.location.latitude = ?2 and s.location.longitude = ?3")
    boolean existsLocationSharedByMe(Long receiverId, Double latitude, Double longitude);
}
