package com.example.user_management_api.service;

import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserRequestDto;
import com.example.user_management_api.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления пользователями.
 * Интерфейс описывает операции для работы с пользователями.
 */
public interface UserService {
    /**
     * Создает нового пользователя.
     * @param requestDto DTO с данными для создания.
     * @return DTO с данными созданного пользователя.
     */
    UserResponseDto createUser(CreateUserRequestDto requestDto);

    /**
     * Находит пользователя по его ID.
     * @param id UUID пользователя.
     * @return DTO с данными найденного пользователя.
     */
    UserResponseDto getUserById(UUID id);

    /**
     * Возвращает список всех пользователей.
     * @return Список DTO с данными всех пользователей.
     */
    List<UserResponseDto> getAllUsers();
    /**
     * Обновляет существующего пользователя.
     * @param id ID пользователя для обновления.
     * @param requestDto DTO с новыми данными.
     * @return DTO с обновленными данными пользователя.
     */
    UserResponseDto updateUser(UUID id, UpdateUserRequestDto requestDto);

    /**
     * Удаляет пользователя по его ID.
     * @param id UUID пользователя для удаления.
     */
    void deleteUser(UUID id);
}
