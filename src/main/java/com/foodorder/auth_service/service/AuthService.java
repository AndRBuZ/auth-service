package com.foodorder.auth_service.service;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.response.AuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
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

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
