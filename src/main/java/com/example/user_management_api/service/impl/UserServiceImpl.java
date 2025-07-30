package com.example.user_management_api.service.impl;

import com.example.user_management_api.dto.ChangePasswordRequestDto;
import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserDataRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.UserDataResponseDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.exception.UserNotFoundException;
import com.example.user_management_api.mapper.UserMapper;
import com.example.user_management_api.model.User;
import com.example.user_management_api.model.UserData;
import com.example.user_management_api.model.enums.Role;
import com.example.user_management_api.repository.RefreshTokenRepository;
import com.example.user_management_api.repository.UserRepository;
import com.example.user_management_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        User user = userMapper.toUserEntity(requestDto);
        UserData userData = userMapper.toUserDataEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setUserData(userData);
        userData.setUser(user);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAllWithData(pageable);
        return userPage.map(userMapper::toUserResponseDto);
    }

    @Override
    @Transactional
    public UserDataResponseDto  updateUser(UUID userId, UpdateUserDataRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        UserData userDataToUpdate = user.getUserData();
        if (userDataToUpdate == null) {
            throw new IllegalStateException("User with id " + userId + " does not have associated user data.");
        }
        userMapper.updateUserDataFromDto(requestDto, userDataToUpdate);
        return userMapper.toUserDataDto(userDataToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        // 2. Явно удаляем все связанные RefreshToken'ы.
        refreshTokenRepository.deleteByUser(userToDelete);
        // 3. Теперь удаляем самого пользователя.
        userRepository.delete(userToDelete);
    }

    @Override
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!passwordEncoder.matches(requestDto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        userRepository.save(user);
    }

    @Override
    public UserContactInfoResponseDto getUserContactInfo(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserContactInfoDto(user);
    }

    @Override
    public UserDataResponseDto getUserData(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserDataDto(user.getUserData());
    }

    @Override
    @Transactional
    public void setAvatar(UUID userId, String avatarKey) {
        UserData userData = findUserDataByUserId(userId);
        userData.setAvatarKey(avatarKey);

    }

    @Override
    @Transactional
    public String removeAvatar(UUID userId) {
        UserData userData = findUserDataByUserId(userId);
        String oldAvatarKey = userData.getAvatarKey();
        userData.setAvatarKey(null);
        return oldAvatarKey;
    }

    private UserData findUserDataByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        UserData userData = user.getUserData();
        if (userData == null) {
            throw new IllegalStateException("User data not found for user with id: " + userId);
        }
        return userData;
    }
}
