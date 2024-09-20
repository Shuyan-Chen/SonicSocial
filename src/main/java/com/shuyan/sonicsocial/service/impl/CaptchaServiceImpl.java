package com.shuyan.sonicsocial.service.impl;


import com.shuyan.sonicsocial.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private final static String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    public boolean verifyCaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        HashMap<Object, Object> body = new HashMap<>();
        body.put("secret", recaptchaSecret);
        body.put("response", recaptchaResponse);

        ResponseEntity<Map> recaptchResponseEntity = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, body, Map.class);
        Map<String, Object> responseBody = recaptchResponseEntity.getBody();
        Boolean success = (Boolean) responseBody.get("success");

        return success != null && success;
    }
}

