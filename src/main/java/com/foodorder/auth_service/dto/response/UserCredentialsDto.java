package com.foodorder.auth_service.dto.response;

import lombok.Getter;

@Getter
public class UserCredentialsDto {
    private long id;
    private String name;
    private String email;
    private String password;
}
