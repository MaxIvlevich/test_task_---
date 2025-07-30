package com.example.user_management_api.service.impl;

import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserDataRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.UserDataResponseDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.exception.UserNotFoundException;
import com.example.user_management_api.mapper.UserMapper;
import com.example.user_management_api.model.User;
import com.example.user_management_api.model.UserData;
import com.example.user_management_api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("createUser должен успешно создать и сохранить нового пользователя")
    void createUser_shouldSuccessfullyCreateAndSaveNewUser() {
        // 1. Входные данные
        var requestDto = new CreateUserRequestDto(
                "testuserusername", "testuserLastname", "testuserFirstname", null, LocalDate.now(),
                "test@example.com", "+12345", "11235813");

        // 2. Объекты, которые "вернет" маппер
        var userEntity = new User();
        var userDataEntity = new UserData();

        // 3. Настраиваем поведение моков
        when(userMapper.toUserEntity(requestDto)).thenReturn(userEntity);
        when(userMapper.toUserDataEntity(requestDto)).thenReturn(userDataEntity);
        when(passwordEncoder.encode("11235813")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(mock(UserResponseDto.class));


        // --- ACT  ---
        userService.createUser(requestDto);


        // --- ASSERT  ---
        // 1. Используем ArgumentCaptor, чтобы "поймать" объект, который был передан в метод save
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        // 2. Проверяем, что с пойманным объектом все в порядке
        assertThat(capturedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(capturedUser.getRoles()).contains(com.example.user_management_api.model.enums.Role.ROLE_USER);

        // 3. Проверяем, что двусторонняя связь установлена правильно
        assertThat(capturedUser.getUserData()).isNotNull();
        assertThat(capturedUser.getUserData()).isEqualTo(userDataEntity);
        assertThat(userDataEntity.getUser()).isEqualTo(userEntity);

        // 4. Убедимся, что все нужные методы были вызваны
        verify(passwordEncoder, times(1)).encode("11235813");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUserResponseDto(any(User.class));
    }

    @Test
    @DisplayName("getUserContactInfo должен вернуть DTO с контактной информацией, если пользователь найден")
    void getUserContactInfo_whenUserExists_shouldReturnContactInfoDto() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        User foundUser = new User(); // Создаем "найденного" пользователя
        foundUser.setId(userId);

        UserContactInfoResponseDto expectedDto = new UserContactInfoResponseDto(userId, "test", "test@test.com", null, null);

        // Настраиваем моки: репозиторий находит пользователя, маппер его преобразует
        when(userRepository.findById(userId)).thenReturn(Optional.of(foundUser));
        when(userMapper.toUserContactInfoDto(foundUser)).thenReturn(expectedDto);

        // --- ACT ---
        UserContactInfoResponseDto actualDto = userService.getUserContactInfo(userId);

        // --- ASSERT ---
        assertThat(actualDto).isNotNull();
        assertThat(actualDto.id()).isEqualTo(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUserContactInfoDto(foundUser);
    }

    @Test
    @DisplayName("getUserContactInfo должен бросить UserNotFoundException, если пользователь не найден")
    void getUserContactInfo_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        // Настраиваем мок репозитория: он возвращает пустой Optional
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Проверяем, что вызов метода приводит к выбросу нужного исключения
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserContactInfo(userId);
        });

        // Убедимся, что маппер даже не был вызван
        verify(userMapper, never()).toUserContactInfoDto(any());
    }

    @Test
    @DisplayName("deleteUser должен вызвать deleteById, если пользователь существует")
    void deleteUser_whenUserExists_shouldCallDeleteById() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        // Настраиваем мок: проверка на существование возвращает true
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // --- ACT ---
        userService.deleteUser(userId);

        // --- ASSERT ---
        // Проверяем, что метод deleteById был вызван ровно один раз
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser должен бросить UserNotFoundException, если пользователь не существует")
    void deleteUser_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        // Настраиваем мок: проверка на существование возвращает false
        when(userRepository.existsById(userId)).thenReturn(false);

        // --- ACT & ASSERT ---
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        // Убедимся, что метод удаления даже не был вызван
        verify(userRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("updateUserData должен обновить данные пользователя и вернуть DTO")
    void updateUserData_shouldUpdateDataAndReturnDto() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        var requestDto = new UpdateUserDataRequestDto("NewLastName", "NewFirstName", null, null);

        // Создаем существующие сущности, которые якобы лежат в базе
        UserData existingUserData = new UserData();
        existingUserData.setFirstName("OldFirstName");
        existingUserData.setLastName("OldLastName");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserData(existingUserData);

        // Создаем DTO, который якобы вернет маппер
        var responseDto = new UserDataResponseDto(userId, "NewFirstName", "NewLastName", null, null, null);

        // Настраиваем поведение моков:
        // 1. Репозиторий находит пользователя
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // 2. void метод маппера просто ничего не делает (но мы проверим, что он был вызван)
        doNothing().when(userMapper).updateUserDataFromDto(eq(requestDto), any(UserData.class));

        // 3. Репозиторий "сохраняет" пользователя
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // 4. Маппер преобразует обновленные данные в финальный DTO
        when(userMapper.toUserDataDto(existingUserData)).thenReturn(responseDto);


        // --- ACT ---
        UserDataResponseDto actualResponse = userService.updateUser(userId, requestDto);


        // --- ASSERT ---
        // Проверяем, что результат совпадает с ожидаемым
        assertThat(actualResponse).isEqualTo(responseDto);

        // Проверяем, что все нужные методы были вызваны
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUserDataFromDto(requestDto, existingUserData);
        verify(userMapper, times(1)).toUserDataDto(existingUserData);
    }
}
