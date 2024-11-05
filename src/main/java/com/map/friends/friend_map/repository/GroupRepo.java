package com.map.friends.friend_map.repository;

import com.map.friends.friend_map.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<Group,Long> {
    @Query("select count(g) from Group g where g.createdBy = :groupName")
    int countGroupByCreatedBy(String groupName);
}
