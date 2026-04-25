package com.library.management.controller.file;

import com.library.management.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/book-cover")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> uploadBookCover(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.saveBookCover(file);
        return Map.of("url", fileUrl);
    }
}