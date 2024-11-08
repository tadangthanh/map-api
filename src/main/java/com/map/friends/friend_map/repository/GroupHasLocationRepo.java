package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.GroupHasLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupHasLocationRepo extends JpaRepository<GroupHasLocation, Long> {
    @Query("SELECT ghl FROM GroupHasLocation ghl WHERE ghl.group.id = :groupId")
    List<GroupHasLocation> findAllByGroupId(Long groupId);
    @Query("select case when count(ghl) > 0 then true else false end from GroupHasLocation ghl where ghl.createdBy=?1 and ghl.group.id=?2")
    boolean existByCreatedByAndGroupId(String created,Long groupId);
    @Query("select ghl from GroupHasLocation ghl where ghl.location.id = ?1")
    Optional<GroupHasLocation> findByLocationId(Long locationId);
}
