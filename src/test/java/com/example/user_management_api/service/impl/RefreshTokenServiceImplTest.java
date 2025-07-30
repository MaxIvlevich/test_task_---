package com.example.user_management_api.service.impl;

import com.example.user_management_api.exception.TokenRefreshException;
import com.example.user_management_api.model.RefreshToken;
import com.example.user_management_api.model.User;
import com.example.user_management_api.repository.RefreshTokenRepository;
import com.example.user_management_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshTokenServiceImplTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем экземпляр нашего сервиса, передавая моки в конструктор
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, userRepository);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 600000L);
    }

    @Test
    @DisplayName("createRefreshToken должен создать и сохранить новый refresh токен")
    void createRefreshToken_shouldCreateAndSaveToken() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        RefreshToken createdToken = refreshTokenService.createRefreshToken(userId);

        // --- ASSERT ---
        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getUser()).isEqualTo(user);
        assertThat(createdToken.getToken()).isNotNull();
        assertThat(createdToken.getExpiryDate()).isAfter(Instant.now());

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("verifyExpiration не должен бросать исключение, если токен валиден")
    void verifyExpiration_whenTokenIsValid_shouldReturnToken() {
        // --- ARRANGE ---
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        // --- ACT ---
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // --- ASSERT ---
        assertThat(result).isEqualTo(token);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("verifyExpiration должен бросить исключение и удалить токен, если он истек")
    void verifyExpiration_whenTokenIsExpired_shouldThrowExceptionAndDeleleToken() {
        // --- ARRANGE ---
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60)); // Срок истек минуту назад

        // --- ACT & ASSERT ---
        assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        verify(refreshTokenRepository, times(1)).delete(token);
    }
}
