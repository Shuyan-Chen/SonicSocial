package com.shuyan.sonicsocial.repository;


import com.shuyan.sonicsocial.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByUsername(String username);
    boolean findFirstLoginById(long userId);
    @Query(value = "SELECT * FROM user u WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * cos(radians(u.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(u.latitude)))) < :radius",
            nativeQuery = true)
    List<User> findUsersByLocationAndRadius(@Param("latitude") double latitude,
                                            @Param("longitude") double longitude,
                                            @Param("radius") double radius);

}
