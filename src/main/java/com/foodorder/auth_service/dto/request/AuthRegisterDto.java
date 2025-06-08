package com.foodorder.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AuthRegisterDto {
    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "Please provide a valid email address", regexp = ".+@.+\\..+")
    private String email;

    @Size(min = 6)
    private String password;
}
