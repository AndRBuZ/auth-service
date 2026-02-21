package com.foodorder.auth_service.exception;

public class UserInvalidCredentialsException extends RuntimeException {
    public UserInvalidCredentialsException() {
        super("Invalid Credentials");
    }
}
