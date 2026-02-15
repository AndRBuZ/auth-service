package com.foodorder.auth_service.dto.response;

public record AuthResponseDto<T>(boolean success, T data, String message) {}
