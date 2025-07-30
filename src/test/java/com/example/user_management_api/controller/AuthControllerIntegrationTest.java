package com.example.user_management_api.controller;

import com.example.user_management_api.BaseIntegrationTest;
import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    // Очищаем базу данных перед каждым тестом
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/users должен создать нового пользователя и вернуть 201 Created")
    void createUser_shouldCreateUserAndReturn201() {
        // --- ARRANGE ---
        var requestDto = new CreateUserRequestDto(
                "integration_user", "Test", "User", null, LocalDate.now(),
                "integration@test.com", "+99999", "password123");

        // --- ACT ---

        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity("/api/v1/users", requestDto, UserResponseDto.class);

        // --- ASSERT ---

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("integration_user");

        // Проверяем состояние базы данных
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findByUsername("integration_user")).isPresent();
    }
}
