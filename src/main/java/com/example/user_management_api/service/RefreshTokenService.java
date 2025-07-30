package com.example.user_management_api.service;

import com.example.user_management_api.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    /**
     * Ищет refresh-токен по его строковому значению.
     *
     * @param token Строка токена.
     * @return Optional, содержащий токен, если он найден; иначе — пустой.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Создаёт и сохраняет новый refresh-токен для указанного пользователя.
     *
     * @param userId ID пользователя, для которого создаётся токен.
     * @return Сгенерированный и сохранённый refresh-токен.
     */
    RefreshToken createRefreshToken(UUID userId);
    /**
     * Проверяет срок действия токена. Если срок истёк — выбрасывается исключение.
     *
     * @param token Refresh-токен для проверки.
     * @return Тот же токен, если он действителен.
     */
    RefreshToken verifyExpiration(RefreshToken token);

    /**
     * Удаляет все refresh-токен, связанный с указанным пользователем.
     *
     * @param userId ID пользователя, чьи токены нужно удалить.
     */
    void deleteByUserId(UUID userId);
}
