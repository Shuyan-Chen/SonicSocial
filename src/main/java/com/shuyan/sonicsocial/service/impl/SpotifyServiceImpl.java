package com.shuyan.sonicsocial.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyan.exception.SpotifyAuthException;
import com.shuyan.service.SpotifyService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SpotifyServiceImpl implements SpotifyService {
    private static final String CLIENT_ID = "ae4496814a644c10b1c447a2e54b67a6";
    private static final String CLIENT_SECRET= "fffc170f03594804ac6ee266395f2276";
    private static final String REDIRECT_URI = "http://localhost:3434";
    private static final String SCOPE = "user-library-read";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    public String connectSpotify(String userId) {
        String authUrl = "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);
        return authUrl;
    }

    public String handleCallback(String code) {
        String accessToken = getAccessToken(code);
        return accessToken;
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, String.class);


        return parseAccessToken(response.getBody());
    }

    public String parseAccessToken(String responseBody) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("access_token").asText();
            }catch (Exception e){
                throw new SpotifyAuthException("Failed to parse access token response: " + e);
            }
    }

}
