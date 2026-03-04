package com.foodorder.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private boolean success;
    private UserPublicDto data;
    private String token;
    private String refreshToken;
    private String message;
}
