package com.example.user_management_api.service.impl;

import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserRequestDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.exception.UserNotFoundException;
import com.example.user_management_api.mapper.UserMapper;
import com.example.user_management_api.model.User;
import com.example.user_management_api.model.enums.Role;
import com.example.user_management_api.repository.UserRepository;
import com.example.user_management_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        User user = userMapper.toEntity(requestDto);

        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRoles(Set.of(Role.ROLE_USER));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(userMapper.toDto(user));
        }
        return userDtos;
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserRequestDto requestDto) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        // Используем маппер для обновления сущности
        userMapper.updateUserFromDto(requestDto, userToUpdate);
        User updatedUser = userRepository.save(userToUpdate);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
