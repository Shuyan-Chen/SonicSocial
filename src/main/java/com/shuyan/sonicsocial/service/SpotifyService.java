package com.shuyan.sonicsocial.service;

import com.shuyan.sonicsocial.entity.Track;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SpotifyService {
    String connectSpotify();
    String handleCallback(String code);
    List<Track> getUserFavorites(String token);
    List<Track> getAudioFeaturesForTracks(List<Track> tracks);

}
