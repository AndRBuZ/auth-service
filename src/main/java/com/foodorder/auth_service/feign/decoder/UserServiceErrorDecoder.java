package com.foodorder.auth_service.feignClient.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.auth_service.exception.UserInvalidCredentialsException;
import com.foodorder.auth_service.exception.UserNotFoundException;
import com.foodorder.auth_service.exception.dto.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class UserServiceErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse message;

        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ErrorResponse.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return switch (response.status()) {
            case 400 -> new UserInvalidCredentialsException();
            case 404 -> new UserNotFoundException();
            default -> errorDecoder.decode(methodKey, response);
        };
    }
}
