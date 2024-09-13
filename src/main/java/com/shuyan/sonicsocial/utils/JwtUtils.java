package com.shuyan.sonicsocial.utils;


import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtUtils {

    private static final String jwtToken = "1234567890p[]l;'";

    public static String createToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, jwtToken);
        String token = jwtBuilder.compact();
        return token;
    }

    public static Map<String, Object> checkToken(String token) {
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String, Object>) parse.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
