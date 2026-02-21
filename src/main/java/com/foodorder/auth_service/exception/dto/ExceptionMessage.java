package com.foodorder.auth_service.exception.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private LocalDateTime timestamp;
    private String message;
}
