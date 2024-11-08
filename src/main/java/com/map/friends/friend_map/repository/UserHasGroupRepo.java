package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.UserHasGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserHasGroupRepo extends JpaRepository<UserHasGroup, Long> {
    @Query("select u from UserHasGroup u where u.user.id = ?1 and u.group.id = ?2")
    Set<UserHasGroup> findAllByUserIdAndGroupId(Long userId, Long groupId);

    @Query("select count(u) from UserHasGroup u where u.user.id = ?1 and u.status = 'JOINED'")
    int countGroupsJoinedByUser(Long userId);

    @Query("select u from UserHasGroup u where u.user.id = ?1")
    List<UserHasGroup> findAllByUserId(Long userId);

    @Query("select u from UserHasGroup u where u.user.id = ?1 and u.group.id = ?2")
    Optional<UserHasGroup> findByUserIdAndGroupId(Long userId, Long groupId);

    @Query("select case when count(u) > 0 then true else false end from UserHasGroup u where u.user.id = ?1 and u.group.id = ?2")
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);
}
