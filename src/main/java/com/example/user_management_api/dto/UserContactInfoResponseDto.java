package com.example.user_management_api.dto;

import com.example.user_management_api.model.enums.Role;

import java.util.Set;
import java.util.UUID;
/**
 * DTO для ответа с контактной информацией пользователя.
 */
public record UserContactInfoResponseDto (
        UUID id,
        String username,
        String email,
        String phoneNumber,
        Set<Role> roles
){
}
