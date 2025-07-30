package com.example.user_management_api.dto.auth;

import java.util.List;
import java.util.UUID;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        UUID id,
        String username,
        String email,
        List<String> roles
) {
}
