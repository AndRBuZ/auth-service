package com.foodorder.auth_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String userId;
    private String refreshToken;

    public JwtResponseDto(String token, String userId, String refreshToken) {
        this.token = token;
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
