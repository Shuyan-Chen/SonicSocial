package com.shuyan.sonicsocial.service.impl;


import com.shuyan.sonicsocial.context.UserContext;
import com.shuyan.sonicsocial.dto.UserLoginDto;
import com.shuyan.sonicsocial.dto.UserRegisterDto;
import com.shuyan.sonicsocial.entity.User;
import com.shuyan.sonicsocial.repository.UserRepository;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.service.UserService;
import com.shuyan.sonicsocial.utils.JwtUtils;
import com.shuyan.sonicsocial.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisCache redisCache;

    @Override
    public Result register(UserRegisterDto userRegisterDto) {

        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            return Result.error("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            return Result.error("Email already exists");
        }

        String encodePsd = passwordEncoder.encode(userRegisterDto.getPassword());
        userRegisterDto.setPassword(encodePsd);
        User user = new User();
        BeanUtils.copyProperties(userRegisterDto,user);
        userRepository.save(user);
        return Result.success();
    }

    @Override
    public Result login(UserLoginDto userLoginDto) {
        String password = userLoginDto.getPassword();
        String username = userLoginDto.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Result.error("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("Invalid username or password");
        }

        user.setFirstLogin(true);
        String token = jwtUtils.generateToken(user.getId());
        redisCache.setCacheObject("JWT" + user.getId(),token,604800000,TimeUnit.MILLISECONDS);
        UserContext.setCurrentUserId(user.getId());
        return Result.success(token);
    }

    @Override
    public boolean isFirstLogin(long userId) {
        return userRepository.findFirstLoginById(userId);
    }
}
