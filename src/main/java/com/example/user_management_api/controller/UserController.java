package com.example.user_management_api.controller;

import com.example.user_management_api.dto.ChangePasswordRequestDto;
import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserDataRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.UserDataResponseDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        UserResponseDto createdUser = userService.createUser(requestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        Page<UserResponseDto> usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }


    @PutMapping("/{id}/data")
    public ResponseEntity<UserDataResponseDto> updateUserData(@PathVariable UUID id, @Valid @RequestBody UpdateUserDataRequestDto requestDto) {
        UserDataResponseDto updatedUserData = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updatedUserData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id,
                                               @Valid @RequestBody ChangePasswordRequestDto requestDto) {
        userService.changePassword(id, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/contact-info")
    public ResponseEntity<UserContactInfoResponseDto> getUserContactInfo(@PathVariable UUID id) {
        UserContactInfoResponseDto contactInfo = userService.getUserContactInfo(id);
        return ResponseEntity.ok(contactInfo);
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<UserDataResponseDto> getUserData(@PathVariable UUID id) {
        UserDataResponseDto userData = userService.getUserData(id);
        return ResponseEntity.ok(userData);
    }

}
