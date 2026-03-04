package com.foodorder.auth_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponseDto {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private int expiresIn = 900;

    public RefreshTokenResponseDto(String refreshToken, String token) {
        this.refreshToken = refreshToken;
        this.token = token;
    }
}
