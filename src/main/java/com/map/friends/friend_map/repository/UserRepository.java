package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsUserByEmail(String email);
    boolean existsUserByGoogleId(String googleId);
    @Query("select u from User u where u.googleId = ?1")
    Optional<User> findByGoogleId(String googleId);
    @Query("select u from User u where lower(u.email) = lower(?1)")
    Optional<User> findByEmail(String email);
}
