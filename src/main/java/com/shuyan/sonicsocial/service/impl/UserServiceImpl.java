package com.shuyan.sonicsocial.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyan.Captcha;
import com.shuyan.dto.UserLoginDTO;
import com.shuyan.dto.UserRegisterDTO;
import com.shuyan.entity.Track;
import com.shuyan.entity.User;
import com.shuyan.exception.TrackParsingException;
import com.shuyan.repository.UserRepository;
import com.shuyan.result.Result;
import com.shuyan.service.CaptchaService;
import com.shuyan.service.UserService;
import com.shuyan.utils.JwtUtils;
import com.shuyan.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisCache redisCache;


    public Result register(UserRegisterDTO userRegisterDTO) {

        String password = userRegisterDTO.getPassword();
        String salt = userRegisterDTO.getSalt();
        String md5Password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        userRegisterDTO.setPassword(md5Password);

        BufferedImage captchaImage = captchaService.getCaptcha(uuid);
        Captcha captcha = new Captcha();
        captcha.setCode(userRegisterDTO.getCaptchaCode());
        captcha.setUuid(uuid);

        if (!captchaService.validate(captcha)){
            return false;
        }

        if (userRepository.findByUsername(userRegisterDTO.getUsername())){
            throw new RuntimeException("Username already exist");
        }
        if ((userRepository.findByEmail(userRegisterDTO.getEmail()))){
            throw new RuntimeException("Email already exist");
        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        userRepository.save(user);
        return Result.success();
    }


    public Result login(UserLoginDTO userLoginDTO) {
        String password = userLoginDTO.getPassword();
        String username = userLoginDTO.getUsername();
        User user = userRepository.getUserByUsername(username);
        String md5Password = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes());

        if (!md5Password.equals(user.getPassword())) {
            return Result.error("Login failed");
        }
        String token = JwtUtils.createToken(user.getId());
        redisCache.setCacheObject("TOKEN_" + token, JSON.toJSONString(user), 1, TimeUnit.DAYS);
        return Result.success(token);
    }

    public User checkToken(String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        Map<String, Object> map = JwtUtils.checkToken(token);
        if (map == null){
            return null;
        }
        String userJson = redisCache.getCacheObject("TOKEN_" + token);
        if (StringUtils.isEmpty(userJson)){
            return null;
        }
        User user = JSON.parseObject(userJson,User.class);
        return user;
    }

    public User getById(long id) {
        User user = userRepository.getById(id);
        return user;
    }

    @Override
    public Map<String, Object> analyzeUserMusicTaste(String userId, List<Track> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return handleInsufficientData(userId);
        }

        double totalEnergy = 0;
        double totalDanceability = 0;
        Map<String, Integer> genreFrequency = new HashMap<>();
        Set<String> artists = new HashSet<>();
        double minTempo = Double.MAX_VALUE;
        double maxTempo = Double.MIN_VALUE;

        for (Track track : tracks) {
            totalEnergy += track.getEnergy();
            totalDanceability += track.getDanceability();
            genreFrequency.put(track.getGenres(), genreFrequency.getOrDefault(track.getGenres().get(0), 0) + 1); // 假设每个 Track 有一个主要流派
            artists.add(track.getArtist());
            minTempo = Math.min(minTempo, track.getTempo());
            maxTempo = Math.max(maxTempo, track.getTempo());
        }

        int trackCount = tracks.size();
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("mostFrequentGenres", getMostFrequentGenres(genreFrequency));
        preferences.put("averageEnergy", totalEnergy / trackCount);
        preferences.put("averageDanceability", totalDanceability / trackCount);
        preferences.put("favoriteArtists", new ArrayList<>(artists));
        preferences.put("tempoRange", Map.of("min", minTempo, "max", maxTempo));

        return Map.of("userId", userId, "preferences", preferences);

    }

    private Map<String, Object> handleInsufficientData(String userId) {
        return Map.of("userId", userId, "preferences", "Insufficient data to analyze.");
    }


    public List<Track> getUserFavorites(String id, String accessToken) {
        String favoritesUrl = "https://api.spotify.com/v1/me/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(favoritesUrl, HttpMethod.GET, requestEntity, String.class);
        return parseTracks(response.getBody());
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
                tracks.add(new Track(trackId, name, artist));
            }
        } catch (Exception e) {
            throw new TrackParsingException("Failed to parse tracks from JSON response"+ e);
        }
        return tracks;
    }


}
