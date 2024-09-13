package com.shuyan.sonicsocial.service;

import com.shuyan.dto.UserLoginDTO;
import com.shuyan.dto.UserRegisterDTO;
import com.shuyan.entity.Track;
import com.shuyan.entity.User;
import com.shuyan.result.Result;

import java.util.List;
import java.util.Map;

public interface UserService {

    Result register(UserRegisterDTO userRegisterDTO);

    Result login(UserLoginDTO userLoginDTO);

    User checkToken(String token);

    List<Track> getUserFavorites(String id, String accessToken);

    User getById(long id);

    Map<String, Object> analyzeUserMusicTaste(String userId, List<Track> tracks);
}
