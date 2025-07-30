package com.example.user_management_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;

public record CreateUserRequestDto (
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "First name cannot be blank")
        String firstName,

        String patronymic,

        @NotNull(message = "Date of birth cannot be null")
        LocalDate dateOfBirth,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password

){
}
