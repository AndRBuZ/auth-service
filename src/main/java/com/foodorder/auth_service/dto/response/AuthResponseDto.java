package com.foodorder.auth_service.dto.response;

public record AuthResponseDto(boolean success, UserPublicDto data, String message) {
}
