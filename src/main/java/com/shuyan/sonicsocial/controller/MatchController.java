package com.shuyan.sonicsocial.controller;

import com.shuyan.sonicsocial.context.UserContext;
import com.shuyan.sonicsocial.entity.User;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/matches")
    public Result<List<User>> getMatchingUsers(@RequestParam double latitude, @RequestParam double longitude,
                                               @RequestParam(defaultValue = "50") double radius) {

        return Result.success(matchService.findMatchingUsers(latitude,longitude, radius));
    }
}
