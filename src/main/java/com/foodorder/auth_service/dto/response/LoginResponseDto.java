package com.foodorder.auth_service.dto.response;

public record LoginResponseDto(boolean success, UserPublicDto data, String token, String refreshToken, String message) {
}
