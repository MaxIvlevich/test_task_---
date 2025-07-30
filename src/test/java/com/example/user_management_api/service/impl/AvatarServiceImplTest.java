package com.example.user_management_api.service.impl;

import com.example.user_management_api.service.FileStorageService;
import com.example.user_management_api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceImplTest {
    @Mock
    private UserService userService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    @Test
    @DisplayName("uploadAvatar должен удалить старый аватар, загрузить новый и обновить пользователя")
    void uploadAvatar_shouldDeleteOldAndUploadNew() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "some-image-bytes".getBytes());
        String oldAvatarKey = "old-key";
        String newAvatarKey = "new-key";

        // 1. При удалении userService возвращает ключ старого аватара
        when(userService.removeAvatar(userId)).thenReturn(oldAvatarKey);
        // 2. fileStorageService успешно загружает новый файл и возвращает новый ключ
        when(fileStorageService.uploadFile(newAvatarFile)).thenReturn(newAvatarKey);

        // --- ACT ---
        avatarService.uploadAvatar(userId, newAvatarFile);

        // --- ASSERT ---
        // Проверяем, что все нужные методы были вызваны в правильном порядке
        verify(userService, times(1)).removeAvatar(userId);
        verify(fileStorageService, times(1)).deleteFile(oldAvatarKey);
        verify(fileStorageService, times(1)).uploadFile(newAvatarFile);
        verify(userService, times(1)).setAvatar(userId, newAvatarKey);
    }

    @Test
    @DisplayName("uploadAvatar не должен пытаться удалить старый файл, если его не было")
    void uploadAvatar_whenNoOldAvatar_shouldOnlyUploadNew() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "some-image-bytes".getBytes());
        String newAvatarKey = "new-key";

        // Настраиваем поведение моков:
        // 1. userService.removeAvatar возвращает null (старого аватара не было)
        when(userService.removeAvatar(userId)).thenReturn(null);
        when(fileStorageService.uploadFile(newAvatarFile)).thenReturn(newAvatarKey);

        // --- ACT ---
        avatarService.uploadAvatar(userId, newAvatarFile);

        // --- ASSERT ---
        verify(userService, times(1)).removeAvatar(userId);
        // Проверяем, что метод удаления файла НЕ был вызван
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(fileStorageService, times(1)).uploadFile(newAvatarFile);
        verify(userService, times(1)).setAvatar(userId, newAvatarKey);
    }

    @Test
    @DisplayName("deleteAvatar должен удалить ключ у пользователя и сам файл")
    void deleteAvatar_shouldRemoveKeyAndFile() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        String avatarKey = "key-to-delete";

        // Настраиваем мок: userService.removeAvatar возвращает ключ удаляемого файла
        when(userService.removeAvatar(userId)).thenReturn(avatarKey);

        // --- ACT ---
        avatarService.deleteAvatar(userId);

        // --- ASSERT ---
        verify(userService, times(1)).removeAvatar(userId);
        verify(fileStorageService, times(1)).deleteFile(avatarKey);
    }
}
