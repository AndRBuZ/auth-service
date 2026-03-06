package com.foodorder.auth_service.security.jwt.service;

import com.foodorder.auth_service.dto.response.RefreshTokenResponseDto;
import com.foodorder.auth_service.entity.RefreshToken;
import com.foodorder.auth_service.exception.TokenNotFoundException;
import com.foodorder.auth_service.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtService jwtService;
    private final RefreshTokenRepository repository;

    public RefreshTokenResponseDto refresh(String refreshToken) {
        Claims claims = validateJwt(refreshToken);

        long userId = Long.parseLong(claims.getSubject());

        RefreshToken session = loadSession(userId);

        verifySession(refreshToken, session);

        rotateSession(session);

        return issueNewToken(userId);
    }

    public void saveNewSession(long userId, String refreshedToken) {
        RefreshToken token = new RefreshToken(userId,
                hashRefreshToken(refreshedToken),
                Instant.now(),
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        repository.save(token);
    }

    public void deleteSession(String refreshedToken) {
        Claims claims = validateJwt(refreshedToken);

        RefreshToken session = loadSession(Long.parseLong(claims.getSubject()));

        rotateSession(session);
    }

    private Claims validateJwt(String token) {
        if (!jwtService.validateRefreshToken(token)) throw new BadCredentialsException("Invalid refresh token");

        return jwtService.extractRefreshClaims(token);
    }

    private RefreshToken loadSession(long userId) {
        return repository.getByUserId(userId).orElseThrow(TokenNotFoundException::new);
    }

    private void verifySession(String token, RefreshToken session) {
        if (session.isRevoked()) throw new BadCredentialsException("Token revoked");

        if (session.getExpiredAt().isBefore(Instant.now())) throw new BadCredentialsException("Token expired");

        if (!hashRefreshToken(token).equals(session.getToken_hash())) throw new BadCredentialsException("Token mismatch");
    }

    private void rotateSession(RefreshToken session) {
        session.setRevoked(true);
        repository.save(session);
    }

    private RefreshTokenResponseDto issueNewToken(long userId) {
        String access = jwtService.generateAccessToken(Long.toString(userId));
        String refresh = jwtService.generateRefreshToken(Long.toString(userId));

        saveNewSession(userId, refresh);

        return new RefreshTokenResponseDto(refresh, access);
    }

    private String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
