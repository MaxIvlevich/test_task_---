package com.example.user_management_api.service.impl;

import com.example.user_management_api.service.AvatarService;
import com.example.user_management_api.service.FileStorageService;
import com.example.user_management_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void uploadAvatar(UUID userId, MultipartFile file) {

        deleteAvatar(userId);
        String newAvatarKey = fileStorageService.uploadFile(file);
        try {
            userService.setAvatar(userId, newAvatarKey);
        } catch (Exception e) {
            fileStorageService.deleteFile(newAvatarKey);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAvatar(UUID userId) {
        String oldAvatarKey = userService.removeAvatar(userId);
        if (oldAvatarKey != null) {
            fileStorageService.deleteFile(oldAvatarKey);
        }
    }
}
