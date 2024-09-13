package com.shuyan.sonicsocial.controller;

import com.shuyan.dto.UserLoginDTO;
import com.shuyan.dto.UserRegisterDTO;
import com.shuyan.entity.Track;
import com.shuyan.entity.User;
import com.shuyan.result.Result;
import com.shuyan.service.UserService;
import com.shuyan.sonicsocial.dto.UserRegisterDTO;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.service.UserService;
import com.shuyan.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation("userRegister")
    public Result register(@RequestBody @Validated UserRegisterDTO userRegisterDTO){
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/login")
    @ApiOperation("userLogin")
    public Result<UserLoginVO> login(@RequestBody @Validated UserLoginDTO userloginDTO){
        return userService.login(userloginDTO);
    }

    @GetMapping("{id}/favorites")
    @ApiOperation("user favorites")
    public Result<List<Track>> getUserFavorites(@PathVariable String id, @RequestParam String accessToken){
        List<Track> userFavorites = userService.getUserFavorites(id, accessToken);
        return Result.success(userFavorites);
    }

    @GetMapping("{id}")
    @ApiOperation("get user info")
    public Result<User> getById(@PathVariable long id){
        return Result.success(userService.getById(id));
    }

    @PostMapping("/{id}/analyze")
    @ApiOperation("analyze user music taste")
    public Result<Map<String, Object>> analyzeUserMusicTaste(@PathVariable String userId, @RequestBody List<Track> tracks) {
        Map<String, Object> analysisResult = userService.analyzeUserMusicTaste(userId, tracks);
        return Result.success(analysisResult);
    }



}
