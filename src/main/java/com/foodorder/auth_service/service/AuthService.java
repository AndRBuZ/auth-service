package com.foodorder.auth_service.service;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.RefreshTokenRequestDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.request.UserLoginDto;
import com.foodorder.auth_service.dto.response.*;
import com.foodorder.auth_service.exception.UserInvalidCredentialsException;
import com.foodorder.auth_service.feign.client.UserRestClient;
import com.foodorder.auth_service.mapper.UserMapper;
import com.foodorder.auth_service.repository.RefreshTokenRepository;
import com.foodorder.auth_service.security.jwt.service.JwtService;
import com.foodorder.auth_service.security.jwt.service.RefreshTokenService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRestClient userRestClient;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRestClient userRestClient, UserMapper userMapper, JwtService jwtService, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService) {
        this.userRestClient = userRestClient;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponseDto register(AuthRegisterDto dto) {
        String password = hashPassword(dto.password());

        UserCreateDto userCreateDto = userMapper.toCreateDto(dto, password);

        UserPublicDto userPublicDto = userRestClient.createUser(userCreateDto);

        return new AuthResponseDto(true, userPublicDto, "Registration success");
    }

    public LoginResponseDto login(UserLoginDto dto) {
        UserCredentialsDto userResponse = userRestClient.getUserCredentialsByEmail(dto.email());

        UserPublicDto user = new UserPublicDto(
                userResponse.id(),
                userResponse.name(),
                userResponse.email()
        );

        if (!BCrypt.checkpw(dto.password(), userResponse.password())) {
            throw new UserInvalidCredentialsException();
        }

        String jwt = jwtService.generateAccessToken(user.id().toString());
        String refreshToken = jwtService.generateRefreshToken(user.id().toString());

        refreshTokenService.saveNewSession(userResponse.id(), refreshToken);

        return new LoginResponseDto(true, user, jwt, refreshToken, "Login successful");
    }

    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto dto) {
        return refreshTokenService.refresh(dto.refreshToken());
    }

    public void logout(RefreshTokenRequestDto dto) {
        refreshTokenService.deleteSession(dto.refreshToken());
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
