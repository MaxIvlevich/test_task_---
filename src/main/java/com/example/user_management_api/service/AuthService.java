package com.example.user_management_api.service;

import com.example.user_management_api.dto.auth.JwtResponse;
import com.example.user_management_api.dto.auth.LoginRequest;
import com.example.user_management_api.dto.auth.TokenRefreshRequest;
import com.example.user_management_api.dto.auth.TokenRefreshResponse;

public interface AuthService {

    /**
     * Аутентифицирует пользователя и возвращает пару токенов (access и refresh).
     * @param loginRequest DTO с учетными данными.
     * @return DTO с токенами и информацией о пользователе.
     */
    JwtResponse loginUser(LoginRequest loginRequest);

    /**
     * Обновляет access token с помощью refresh token.
     * @param request DTO с refresh-токеном.
     * @return DTO с новой парой токенов.
     */
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}
