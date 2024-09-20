package com.shuyan.sonicsocial.controller;

import com.shuyan.sonicsocial.context.UserContext;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.service.SpotifyService;
import com.shuyan.sonicsocial.service.UserService;
import com.shuyan.sonicsocial.utils.RedisCache;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/spotify")
public class SpotifyAuthController {

    @Autowired
    private SpotifyService spotifyService;
    @Autowired
    private UserService userService;

    @GetMapping("/connect")
   public void connectSpotify(HttpServletResponse response) throws IOException {
        Long currentUserId = UserContext.getCurrentUserId();
        if (!userService.isFirstLogin(currentUserId)){
            String authUrl = spotifyService.connectSpotify();
            response.sendRedirect(authUrl);
        }
    }

    @GetMapping("/callback")
    public Result<String> handleCallback(@RequestParam String code) {
        String token = spotifyService.handleCallback(code);
        spotifyService.getUserFavorites(token);
        return Result.success("Spotify authorization successful.");
    }


}
