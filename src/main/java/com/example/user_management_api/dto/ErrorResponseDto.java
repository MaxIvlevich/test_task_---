package com.example.user_management_api.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        int statusCode,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
