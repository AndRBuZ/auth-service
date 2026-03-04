package com.foodorder.auth_service.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.access_secret}")
    private String access_token_secretFilePath;
    private SecretKey accessSecretKey;

    @Value("${jwt.refresh_token}")
    private String refresh_token_secretFilePAth;
    private SecretKey refreshSecretKey;

    @Value("${jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${jwt.refresh-expiration-time}")
    private long jwtRefreshExpiration;

    public String generateAccessToken(String userId) {
        return createToken(userId, "access", accessSecretKey, jwtExpiration);
    }

    public String generateRefreshToken(String userId) {
        return createToken(userId, "refresh", refreshSecretKey, jwtRefreshExpiration);
    }

    public String extractUserIdFromAccess(String token) {
        return extractAccessClaims(token).getSubject();
    }

    public boolean validateAccessToken(String token) {
        try {
            extractAccessClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            extractRefreshClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims extractAccessClaims(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims extractRefreshClaims(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(String userId, String type, SecretKey secretKey, Long expiration) {
        return Jwts.builder()
                .subject(userId)
                .claim("type", type)
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    @PostConstruct
    private void init() throws Exception {
        String accessSecret = Files.readString(Path.of(access_token_secretFilePath));
        accessSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes());

        String refreshSecret = Files.readString(Path.of(refresh_token_secretFilePAth));
        refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }
}
