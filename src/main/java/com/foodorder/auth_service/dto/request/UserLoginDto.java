package com.foodorder.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public record UserLoginDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address", regexp = ".+@.+\\..+")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}
