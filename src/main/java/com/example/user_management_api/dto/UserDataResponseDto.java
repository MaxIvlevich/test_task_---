package com.example.user_management_api.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для ответа с детальной (персональной) информацией пользователя.
 */
public record UserDataResponseDto (
        UUID userId,
        String firstName,
        String lastName,
        String patronymic,
        LocalDate dateOfBirth,
        String avatarKey
){
}
