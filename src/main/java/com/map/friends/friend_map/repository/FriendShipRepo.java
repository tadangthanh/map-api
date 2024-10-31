package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.FriendShip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendShipRepo extends JpaRepository<FriendShip, Long> {
    @Query("select f from FriendShip f where f.target.id = ?1")
    Page<FriendShip> findByTarget(Long targetId,Pageable pageable);

    @Query("select f from FriendShip f where ((f.author.id = ?1 and f.target.id = ?2) or (f.author.id = ?2 and f.target.id = ?1))")
    Optional<FriendShip> findFriendShipBetweenUsers(Long userId1, Long userId2);

    @Query("select f from FriendShip f where f.author.id = ?1 and f.target.id = ?2")
    Optional<FriendShip> findByAuthorAndTarget(Long authorId, Long targetId);


}
