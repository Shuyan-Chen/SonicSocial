package com.shuyan.sonicsocial.controller;

import com.shuyan.service.SpotifyService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class SpotifyAuthController {

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/auth/spotify")
    public void connectSpotify(@RequestParam String userId, HttpServletResponse response) throws IOException {
        String authUrl = spotifyService.connectSpotify(userId);
        response.sendRedirect(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam String code) {
        return ResponseEntity.ok("Access Token: " + spotifyService.handleCallback(code));
    }


}
