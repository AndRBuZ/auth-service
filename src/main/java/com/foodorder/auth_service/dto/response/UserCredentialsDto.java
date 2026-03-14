package com.foodorder.auth_service.dto.response;

public record UserCredentialsDto(long id, String name, String email, String password) {}
