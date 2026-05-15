package com.library.management.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.library.management.dto.file.FileUploadResponseDto;
import com.library.management.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public FileUploadResponseDto saveBookCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException("Image size must not exceed 5 MB");
        }

        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidRequestException("Only JPG, JPEG, PNG and WEBP images are allowed");
        }

        Path tempFile = null;

        try {
            String splashColor = extractDominantColor(file);
            String publicId = "library-management/book-covers/" + UUID.randomUUID();

            tempFile = Files.createTempFile("book-cover-", extension);
            file.transferTo(tempFile.toFile());

            Map uploadResult = cloudinary.uploader().upload(
                    tempFile.toFile(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            String imageUrl = uploadResult.get("secure_url").toString();

            return FileUploadResponseDto.builder()
                    .url(imageUrl)
                    .splashColor(splashColor)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);

        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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