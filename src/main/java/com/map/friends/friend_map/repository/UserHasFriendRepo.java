package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.UserHasFriend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHasFriendRepo extends JpaRepository<UserHasFriend, Long> {
    @Query("SELECT EXISTS (SELECT 1 FROM UserHasFriend u WHERE (u.userA.id = ?1 AND u.userB.id = ?2) OR (u.userA.id = ?2 AND u.userB.id = ?1))")
    boolean isFriendBetweenUserAAndUserB(Long userId, Long friendId);
    @Query("SELECT u FROM UserHasFriend u WHERE u.userA.id = ?1 OR u.userB.id = ?1")
    Page<UserHasFriend> getFriends(Long userId,Pageable pageable);
    @Query("SELECT u FROM UserHasFriend u WHERE u.userA.id = ?1 OR u.userB.id = ?1")
    List<UserHasFriend> getFriends(Long userId);

    @Query("SELECT u FROM UserHasFriend u WHERE (u.userA.id = ?1 AND u.userB.id = ?2) OR (u.userA.id = ?2 AND u.userB.id = ?1)")
    Optional<UserHasFriend> findFriendShipBetweenUsers(Long userId1, Long userId2);
}
