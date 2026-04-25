package com.library.management.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveBookCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        try {
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path coversPath = Paths.get(uploadDir, "covers");

            Files.createDirectories(coversPath);

            Path targetPath = coversPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/covers/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store book cover", e);
        }
    }
}