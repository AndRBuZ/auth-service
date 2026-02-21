package com.foodorder.auth_service.feignClient;

import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.response.UserCredentialsDto;
import com.foodorder.auth_service.dto.response.UserPublicDto;
import com.foodorder.auth_service.feignClient.decoder.UserServiceErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "http://user-service:8081", configuration = UserServiceErrorDecoder.class)
public interface UserRestClient {
    @GetMapping("/users/email/{email}")
    UserCredentialsDto getUserCredentialsByEmail(
            @PathVariable("email") String email
    );

    @PostMapping("/users")
    UserPublicDto createUser(
            @RequestBody UserCreateDto dto
    );
}
