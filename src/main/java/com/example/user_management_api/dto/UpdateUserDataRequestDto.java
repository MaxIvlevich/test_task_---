package com.example.user_management_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Record для обновления персональных данных пользователя (UserData).
 * Все поля являются необязательными.
 */
public record UpdateUserDataRequestDto(
        String lastName,
        String firstName,
        String patronymic,
        LocalDate dateOfBirth
) {
}
