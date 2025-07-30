package com.example.user_management_api.service;

import com.example.user_management_api.dto.ChangePasswordRequestDto;
import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserDataRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.UserDataResponseDto;
import com.example.user_management_api.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @return траница (Page) с DTO пользователей и информацией о пагинации.
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    /**
     * Обновляет существующего пользователя.
     * @param id ID пользователя для обновления.
     * @param requestDto DTO с новыми данными.
     * @return DTO с обновленными данными пользователя.
     */
    UserDataResponseDto  updateUser(UUID id, UpdateUserDataRequestDto requestDto);

    /**
     * Удаляет пользователя по его ID.
     * @param id UUID пользователя для удаления.
     */
    void deleteUser(UUID id);

    /**
     * Изменяет пароль пользователя.
     * @param id ID пользователя, которому меняем пароль.
     * @param requestDto DTO с старым и новым паролями.
     */
    void changePassword(UUID id, ChangePasswordRequestDto requestDto);
    /**
     * Возвращает контактную информацию пользователя.
     *
     * @param id ID пользователя, чью контактную информацию нужно получить.
     * @return DTO с контактными данными пользователя (телефон, email и т.п.).
     */
    UserContactInfoResponseDto getUserContactInfo(UUID id);

    /**
     * Возвращает детальную информацию о пользователе.
     *
     * @param id ID пользователя, чью детальную информацию нужно получить.
     * @return DTO с подробной информацией о пользователе (например, адрес, фотография и т.д.).
     */
    UserDataResponseDto getUserData(UUID id);

    /**
     * Устанавливает или обновляет ключ аватара для пользователя.
     * @param userId ID пользователя.
     * @param avatarKey ключ файла в хранилище.
     */
    void setAvatar(UUID userId, String avatarKey);

    /**
     * Удаляет ключ аватара у пользователя.
     * @param userId ID пользователя.
     * @return ключ удаленного аватара, если он был, или null.
     */
    String removeAvatar(UUID userId);
}
