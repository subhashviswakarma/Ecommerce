package com.example.ecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;
}
