package com.library.management.service.file;

import com.library.management.dto.file.FileUploadResponseDto;
import com.library.management.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FileUploadResponseDto saveBookCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File is required");
        }

        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidRequestException("Only JPG, JPEG, PNG and WEBP images are allowed");
        }

        try {
            String splashColor = extractDominantColor(file);

            String fileName = UUID.randomUUID() + extension;
            Path coversPath = Paths.get(uploadDir, "covers").toAbsolutePath().normalize();

            Files.createDirectories(coversPath);

            Path targetPath = coversPath.resolve(fileName).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return FileUploadResponseDto.builder()
                    .url("/uploads/covers/" + fileName)
                    .splashColor(splashColor)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store book cover", e);
        }
    }

    private String getFileExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            throw new InvalidRequestException("File extension is missing");
        }

        return originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
    }

    private String extractDominantColor(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new InvalidRequestException("Uploaded file is not a valid image");
        }

        long red = 0;
        long green = 0;
        long blue = 0;
        long count = 0;

        int stepX = Math.max(1, image.getWidth() / 50);
        int stepY = Math.max(1, image.getHeight() / 50);

        for (int y = 0; y < image.getHeight(); y += stepY) {
            for (int x = 0; x < image.getWidth(); x += stepX) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;

                if (alpha < 128) {
                    continue;
                }

                red += (rgb >> 16) & 0xff;
                green += (rgb >> 8) & 0xff;
                blue += rgb & 0xff;
                count++;
            }
        }

        if (count == 0) {
            return "#d8ddd2";
        }

        return String.format(
                "#%02x%02x%02x",
                (int) (red / count),
                (int) (green / count),
                (int) (blue / count)
        );
    }
}