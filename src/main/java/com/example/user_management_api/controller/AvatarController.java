package com.example.user_management_api.controller;

import com.example.user_management_api.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/avatar")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping
    public ResponseEntity<Void> uploadAvatar(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        avatarService.uploadAvatar(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAvatar(@PathVariable UUID userId) {
        avatarService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
