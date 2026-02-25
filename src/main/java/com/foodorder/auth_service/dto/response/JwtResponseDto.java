package com.foodorder.auth_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String userId;

    public JwtResponseDto(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }
}
