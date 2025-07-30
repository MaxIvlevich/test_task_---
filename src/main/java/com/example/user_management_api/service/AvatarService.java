package com.example.user_management_api.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AvatarService {
    /**
     * Загружает и устанавливает аватар для пользователя.
     * Обрабатывает удаление старого аватара.
     * @param userId ID пользователя.
     * @param file Файл нового аватара.
     */
    void uploadAvatar(UUID userId, MultipartFile file);

    /**
     * Удаляет аватар пользователя.
     * @param userId ID пользователя.
     */
    void deleteAvatar(UUID userId);
}
