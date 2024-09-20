package com.shuyan.sonicsocial.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyan.sonicsocial.context.UserContext;
import com.shuyan.sonicsocial.entity.Track;
import com.shuyan.sonicsocial.entity.User;
import com.shuyan.sonicsocial.entity.UserTrack;
import com.shuyan.sonicsocial.exception.SpotifyAuthException;
import com.shuyan.sonicsocial.exception.TrackParsingException;
import com.shuyan.sonicsocial.repository.TrackRepository;
import com.shuyan.sonicsocial.repository.UserRepository;
import com.shuyan.sonicsocial.repository.UserTrackRepository;
import com.shuyan.sonicsocial.service.SpotifyService;
import com.shuyan.sonicsocial.utils.JwtUtils;
import com.shuyan.sonicsocial.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SpotifyServiceImpl implements SpotifyService {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private UserTrackRepository userTrackRepository;
    @Autowired
    private UserRepository userRepository;
    private static final String CLIENT_ID = "ae4496814a644c10b1c447a2e54b67a6";
    private static final String CLIENT_SECRET= "fffc170f03594804ac6ee266395f2276";
    private static final String REDIRECT_URI = "http://localhost:3434";
    private static final String SCOPE = "user-library-read";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String USER_FAVORITES_URL = "https://api.spotify.com/v1/me/top/tracks";
    private static final String TRACKS_AUDIO_FEATURES = "https://api.spotify.com/v1/audio-features?ids=";



    public String connectSpotify() {
        String authUrl = "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);
        return authUrl;
    }

    public String handleCallback(String code) {
        String accessToken = getAccessToken(code);
        redisCache.setCacheObject("Spotify:" + UserContext.getCurrentUserId(), accessToken,3600, TimeUnit.MILLISECONDS);
        return accessToken;
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, requestEntity, String.class);
        return parseAccessToken(response.getBody());
    }

    public String parseAccessToken(String responseBody) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String accessToken = jsonNode.get("access_token").asText();
                return accessToken;
            }catch (Exception e){
                throw new SpotifyAuthException("Failed to parse access token response: " + e);
            }
    }

    @Async
    @Override
    public List<Track> getUserFavorites(String token) {
        HttpHeaders headers = new HttpHeaders();
        Long currentUserId = UserContext.getCurrentUserId();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(USER_FAVORITES_URL, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch user favorites, status code: " + response.getStatusCode());
        }

        List<Track> tracks = parseTracks(response.getBody());
        List<Track> enrichedTracks = getAudioFeaturesForTracks(tracks); //limit 100 tracks
        for (Track track: enrichedTracks){
            if (!trackRepository.existsById(track.getId())){
                trackRepository.save(track);
            }
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserTrack userTrack = new UserTrack();
            userTrack.setUser(user);
            userTrack.setTrack(track);

            userTrackRepository.save(userTrack);
        }
        return enrichedTracks;
    }

    private List<Track> parseTracks(String jsonResponse){
        List<Track> tracks = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("items");

            for (JsonNode item : itemsNode) {
                JsonNode trackNode = item.path("track");
                String trackId = trackNode.path("id").asText();
                String name = trackNode.path("name").asText();
                String artist = trackNode.path("artists").get(0).path("name").asText();
                tracks.add(new Track(trackId,name,artist));
            }
        } catch (Exception e) {
            throw new TrackParsingException("Failed to parse tracks from JSON response"+ e);
        }
        return tracks;
    }

    @Override
    public List<Track> getAudioFeaturesForTracks(List<Track> tracks) {
        List<String> trackIds = tracks.stream()
                .map(track -> String.valueOf(track.getTrackId()))
                .collect(Collectors.toList());
        String ids = String.join(",", trackIds);
        String url = TRACKS_AUDIO_FEATURES + ids;
        String accessToken = redisCache.getCacheObject("Spotify" + UserContext.getCurrentUserId());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return parseAudioFeatures(response.getBody(), tracks);
    }

    private List<Track> parseAudioFeatures(String jsonResponse,List<Track> tracks) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode audioFeaturesArray = rootNode.path("audio_features");

            for (JsonNode featureNode : audioFeaturesArray) {
                String trackId = featureNode.path("id").asText();
                Track track = tracks.stream().filter(t -> t.getTrackId().equals(trackId)).findFirst().orElse(null);
                if (track != null) {
                    track.setDanceability(featureNode.path("danceability").asDouble());
                    track.setEnergy(featureNode.path("energy").asDouble());
                    track.setTempo(featureNode.path("tempo").asDouble());
                    track.setAcousticness(featureNode.path("acousticness").asDouble());
                    track.setInstrumentalness(featureNode.path("instrumentalness").asDouble());
                    track.setLiveness(featureNode.path("liveness").asDouble());
                    track.setLoudness(featureNode.path("loudness").asDouble());
                    track.setSpeechiness(featureNode.path("speechiness").asDouble());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse audio features: " + e);
        }
        return tracks;
    }


}

