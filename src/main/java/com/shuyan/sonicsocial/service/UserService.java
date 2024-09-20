package com.shuyan.sonicsocial.service;


import com.shuyan.sonicsocial.dto.UserLoginDto;
import com.shuyan.sonicsocial.dto.UserRegisterDto;
import com.shuyan.sonicsocial.result.Result;

public interface UserService {

    Result register(UserRegisterDto userRegisterDto);

    Result login(UserLoginDto userLoginDto);

    boolean isFirstLogin(long userId);


}
