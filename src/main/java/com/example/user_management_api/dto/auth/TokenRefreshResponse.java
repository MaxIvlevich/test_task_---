package com.example.user_management_api.dto.auth;

public record TokenRefreshResponse (
        String accessToken,
        String refreshToken
){
}
