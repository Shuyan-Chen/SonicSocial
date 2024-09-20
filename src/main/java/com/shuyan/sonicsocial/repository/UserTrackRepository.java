package com.shuyan.sonicsocial.repository;


import com.shuyan.sonicsocial.entity.Track;
import com.shuyan.sonicsocial.entity.UserTrack;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserTrackRepository  extends JpaRepository<UserTrack, Long> {

    @Query("SELECT ut.track FROM UserTrack ut WHERE ut.user.id = :userId")
    List<Track> findTracksByUserId(@Param("userId") Long userId);

}
