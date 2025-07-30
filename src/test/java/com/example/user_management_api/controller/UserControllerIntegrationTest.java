package com.example.user_management_api.controller;

import com.example.user_management_api.BaseIntegrationTest;
import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.auth.JwtResponse;
import com.example.user_management_api.dto.auth.LoginRequest;
import com.example.user_management_api.repository.RefreshTokenRepository;
import com.example.user_management_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/users/{id}/contact-info должен вернуть 401 Unauthorized, если токен не предоставлен")
    void getContactInfo_withoutToken_shouldReturn401() {
        // --- ARRANGE ---
        String userId = "some-uuid";

        // --- ACT ---
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/users/" + userId + "/contact-info", String.class);

        // --- ASSERT ---
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET /api/v1/users/{id}/contact-info должен вернуть 200 OK с данными, если предоставлен валидный токен")
    void getContactInfo_withValidToken_shouldReturn200AndData() {
        // --- ARRANGE ---
        // 1.  создаем пользователя напрямую в базе

        // Создаем пользователя через публичный эндпоинт
        var createUserDto = new CreateUserRequestDto("testuser", "Doe", "John", null, LocalDate.now(), "test@example.com", "+123", "password");
        ResponseEntity<UserContactInfoResponseDto> createResponse = restTemplate.postForEntity("/api/v1/users", createUserDto, UserContactInfoResponseDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String userId = createResponse.getBody().id().toString();

        // 2. Теперь логинимся под этим пользователем, чтобы получить токен
        var loginDto = new LoginRequest("testuser", "password");
        ResponseEntity<JwtResponse> loginResponse = restTemplate.postForEntity("/api/auth/signin", loginDto, JwtResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String accessToken = loginResponse.getBody().accessToken();

        // 3. Создаем HTTP-заголовки с токеном
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // --- ACT ---
        ResponseEntity<UserContactInfoResponseDto> response = restTemplate.exchange(
                "/api/v1/users/" + userId + "/contact-info",
                HttpMethod.GET,
                entity,
                UserContactInfoResponseDto.class
        );

        // --- ASSERT ---
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testuser");
    }
}
