package com.foodorder.auth_service.exception;

public class UserInvalidCredentialsException extends RuntimeException {
  public UserInvalidCredentialsException(String message) {
    super(message);
  }
}
