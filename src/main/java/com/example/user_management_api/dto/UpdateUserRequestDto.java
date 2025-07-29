package com.example.user_management_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Record для получения данных от клиента при обновлении пользователя.
 * Все поля являются необязательными.
 */
public record UpdateUserRequestDto(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        String lastName,

        String firstName,

        String patronymic,

        LocalDate dateOfBirth,

        @Email(message = "Email should be valid")
        String email,

        String phoneNumber
) {
}
