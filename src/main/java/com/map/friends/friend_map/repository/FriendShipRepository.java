package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.FriendShip;
import com.map.friends.friend_map.entity.FriendShipStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    @Query("select f from FriendShip f where f.author.id = ?1  and f.status = ?2 ")
    Page<FriendShip> findByAuthorAndStatus(Long authorId, FriendShipStatus status, Pageable pageable);

    @Query("select f from FriendShip f where f.author.id = ?1 and f.status = 'PENDING'")
    Optional<FriendShip> findFriendShipByAuthorAndStatusPending(Long authorId);

    @Query("select f from FriendShip f where f.author.id = ?1 and f.target.id = ?2 and f.status = 'PENDING_YOU_ACCEPT'")
    Optional<FriendShip> findByAuthorAndTargetAndStatusPendingYouAccept(Long authorId, Long targetId);

    @Query("select f from FriendShip f where f.author.id = ?1 and f.target.id = ?2 and f.status = 'PENDING'")
    Optional<FriendShip> findByAuthorAndTargetAndStatusPending(Long authorId, Long targetId);

    @Transactional
    @Modifying
    @Query("delete from FriendShip f where f.author.id = ?1 or f.target.id = ?1")
    void deleteFriendShipByAuthorOrTargetId(Long userId);

    @Query("select f.status from FriendShip f where f.author.id = ?1 and f.target.id = ?2")
    Optional<FriendShipStatus> findByAuthorAndTarget(Long authorId, Long targetId);
}
