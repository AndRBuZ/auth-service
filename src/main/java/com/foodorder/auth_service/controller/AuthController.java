package com.foodorder.auth_service.controller;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.UserLoginDto;
import com.foodorder.auth_service.dto.response.AuthResponseDto;
import com.foodorder.auth_service.dto.response.JwtResponseDto;
import com.foodorder.auth_service.dto.response.LoginResponseDto;
import com.foodorder.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterDto dto) {
        AuthResponseDto responseDto = authService.register(dto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        LoginResponseDto responseDto = authService.login(dto);
        return ResponseEntity.ok(new JwtResponseDto(responseDto.getToken(), responseDto.getData().id().toString()));
    }

    @GetMapping("/test")
    public String test(Authentication authentication) {
        return authentication.getName();
    }
}
