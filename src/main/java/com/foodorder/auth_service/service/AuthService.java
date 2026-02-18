package com.foodorder.auth_service.service;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.request.UserLoginDto;
import com.foodorder.auth_service.dto.response.AuthResponseDto;
import com.foodorder.auth_service.dto.response.UserCredentialsDto;
import com.foodorder.auth_service.dto.response.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    private final RestTemplate restTemplate;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<AuthResponseDto> register(AuthRegisterDto dto) {
        String password = hashPassword(dto.getPassword());

        UserCreateDto userCreateDto = new UserCreateDto(
                dto.getName(),
                dto.getEmail(),
                password
        );

        ResponseEntity<AuthResponseDto> resp = restTemplate.postForEntity("http://user-service:8081/users", userCreateDto, AuthResponseDto.class);
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    public ResponseEntity<?> login(UserLoginDto dto) {
        try {
            ResponseEntity<UserCredentialsDto> userResponse = restTemplate.getForEntity(
                    "http://user-service:8081/users/email/" + dto.getEmail(),
                    UserCredentialsDto.class,
                    dto
            );

            UserDto user = new UserDto(
                    userResponse.getBody().getId(),
                    userResponse.getBody().getName(),
                    userResponse.getBody().getEmail()
            );

            HttpStatus status;
            AuthResponseDto resp;
            if (BCrypt.checkpw(dto.getPassword(), userResponse.getBody().getPassword())) {
                status = HttpStatus.OK;
                resp = new AuthResponseDto(true, user, "Login successful");
            } else {
                status = HttpStatus.UNAUTHORIZED;
                resp = new AuthResponseDto(false, null, "Invalid credentials");
            }

            return ResponseEntity.status(status).body(resp);

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
