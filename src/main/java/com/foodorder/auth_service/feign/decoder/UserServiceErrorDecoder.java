package com.foodorder.auth_service.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodorder.auth_service.exception.UserInvalidCredentialsException;
import com.foodorder.auth_service.exception.UserNotFoundException;
import com.foodorder.auth_service.exception.dto.ExceptionMessage;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class UserServiceErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper mapper = new ObjectMapper();
        // Регистрируем модуль для LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // Отключаем сериализацию LocalDateTime в timestamp
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ExceptionMessage message;

        try (InputStream bodyIs = response.body().asInputStream()) {
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return switch (response.status()) {
            case 401 -> new UserInvalidCredentialsException();
            case 404 -> new UserNotFoundException();
            default -> errorDecoder.decode(methodKey, response);
        };
    }
}
