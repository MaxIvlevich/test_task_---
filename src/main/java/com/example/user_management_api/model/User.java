package com.example.user_management_api.model;

import com.example.user_management_api.model.enums.Role;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Пароль пользователя.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Имя пользователя (логин). Может быть null.
     * Если указан, должен быть уникальным.
     */
    @Column(name = "username", unique = true)
    private String username;

    /**
     * Фамилия пользователя. Не может быть пустой.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Имя пользователя. Не может быть пустым.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Отчество пользователя. Может быть пустым (null).
     */
    @Column(name = "patronymic")
    private String patronymic;

    /**
     * Дата рождения пользователя.
     */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Адрес электронной почты пользователя. Должен быть уникальным и не может быть пустым.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Номер телефона пользователя. Должен быть уникальным и не может быть пустым.
     */
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    /**
     * Роль пользователя в системе.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "avatar_key")
    private String avatar;

}
