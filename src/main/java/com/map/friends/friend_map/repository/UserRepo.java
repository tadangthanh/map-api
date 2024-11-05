package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    boolean existsUserByEmail(String email);
    boolean existsUserByGoogleId(String googleId);
    @Query("select u from User u where u.googleId = ?1")
    Optional<User> findByGoogleId(String googleId);
    @Query("select u from User u where lower(u.email) = lower(?1)")
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.id in ?1")
    List<User> findAllByIdIn(Set<Long> userIds);
}
