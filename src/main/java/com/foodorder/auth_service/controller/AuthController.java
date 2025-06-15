package com.foodorder.auth_service.controller;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.response.AuthResponseDto;
import com.foodorder.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRegisterDto dto) {
        return authService.register(dto);
    }
}
