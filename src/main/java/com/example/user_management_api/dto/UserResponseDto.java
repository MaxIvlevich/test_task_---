package com.example.user_management_api.dto;

import com.example.user_management_api.model.enums.Role;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String lastName,
        String firstName,
        String patronymic,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        String avatarKey,
        Set<Role> roles
) {
}
