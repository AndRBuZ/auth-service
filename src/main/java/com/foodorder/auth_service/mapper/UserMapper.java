package com.foodorder.auth_service.mapper;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.response.UserCredentialsDto;
import com.foodorder.auth_service.dto.response.UserPublicDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserPublicDto toPublicDto(UserCredentialsDto dto) {
        return new UserPublicDto(
                dto.id(),
                dto.name(),
                dto.name()
        );
    }

    public UserCreateDto toCreateDto(AuthRegisterDto dto, String hashedPassword) {
        return new UserCreateDto(
                dto.name(),
                dto.email(),
                hashedPassword
        );
    }
}
