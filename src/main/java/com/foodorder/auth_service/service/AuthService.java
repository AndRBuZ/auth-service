package com.foodorder.auth_service.service;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.request.UserLoginDto;
import com.foodorder.auth_service.dto.response.AuthResponseDto;
import com.foodorder.auth_service.dto.response.LoginResponseDto;
import com.foodorder.auth_service.dto.response.UserCredentialsDto;
import com.foodorder.auth_service.dto.response.UserPublicDto;
import com.foodorder.auth_service.exception.UserInvalidCredentialsException;
import com.foodorder.auth_service.feign.client.UserRestClient;
import com.foodorder.auth_service.mapper.UserMapper;
import com.foodorder.auth_service.security.jwt.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRestClient userRestClient;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserRestClient userRestClient, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userRestClient = userRestClient;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDto register(AuthRegisterDto dto) {
        String password = hashPassword(dto.getPassword());

        UserCreateDto userCreateDto = userMapper.toCreateDto(dto, password);

        UserPublicDto userPublicDto = userRestClient.createUser(userCreateDto);

        return new AuthResponseDto(true, userPublicDto, "Registration success");
    }

    public LoginResponseDto login(UserLoginDto dto) {
        UserCredentialsDto userResponse = userRestClient.getUserCredentialsByEmail(dto.getEmail());

        UserPublicDto user = new UserPublicDto(
                userResponse.getId(),
                userResponse.getName(),
                userResponse.getEmail()
        );

        if (!BCrypt.checkpw(dto.getPassword(), userResponse.getPassword())) {
            throw new UserInvalidCredentialsException();
        }

        String jwt = jwtUtil.generateToken(user.id().toString());

        return new LoginResponseDto(true, user, jwt,"Login successful");
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
