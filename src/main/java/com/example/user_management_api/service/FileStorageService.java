package com.example.user_management_api.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для управления файлами в объектном хранилище.
 */
public interface FileStorageService {
    /**
     * Загружает файл в хранилище.
     * @param file загружаемый файл.
     * @return уникальный ключ (имя) сохраненного файла.
     */
    String uploadFile(MultipartFile file);

    /**
     * Удаляет файл из хранилища по его ключу.
     * @param fileKey ключ (имя) файла для удаления.
     */
    void deleteFile(String fileKey);
}
