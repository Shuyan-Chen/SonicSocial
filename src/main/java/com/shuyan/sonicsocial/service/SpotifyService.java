package com.shuyan.sonicsocial.service;

public interface SpotifyService {
    String connectSpotify(String userId);
    String handleCallback(String code);

}
