package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.UserHasFriend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasFriendRepository extends JpaRepository<UserHasFriend, Long> {
    @Query("SELECT EXISTS (SELECT 1 FROM UserHasFriend u WHERE (u.userA.id = ?1 AND u.userB.id = ?2) OR (u.userA.id = ?2 AND u.userB.id = ?1))")
    boolean isFriendBetweenUserAAndUserB(Long userId, Long friendId);
    @Query("SELECT u FROM UserHasFriend u WHERE u.userA.id = ?1 OR u.userB.id = ?1")
    Page<UserHasFriend> getFriends(Long user,Pageable pageable);
}
