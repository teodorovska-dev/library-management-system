package com.library.management.service.file;

import com.library.management.dto.file.FileUploadResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FileUploadResponseDto saveBookCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return FileUploadResponseDto.builder()
                    .url("")
                    .splashColor("#d8ddd2")
                    .build();
        }

        try {
            String splashColor = extractDominantColor(file);

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

            return FileUploadResponseDto.builder()
                    .url("/uploads/covers/" + fileName)
                    .splashColor(splashColor)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store book cover", e);
        }
    }

    private String extractDominantColor(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            return "#d8ddd2";
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

        int avgRed = (int) (red / count);
        int avgGreen = (int) (green / count);
        int avgBlue = (int) (blue / count);

        return String.format("#%02x%02x%02x", avgRed, avgGreen, avgBlue);
    }
}