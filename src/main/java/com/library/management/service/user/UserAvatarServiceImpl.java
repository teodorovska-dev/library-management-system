package com.library.management.service.user;

import com.library.management.dto.user.UserProfileResponseDto;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Override
    public UserProfileResponseDto uploadCurrentUserAvatar(MultipartFile file) {
        try {
            User user = currentUserService.getCurrentAuthenticatedUser();

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            String fileName = UUID.randomUUID() + extension;
            Path uploadDir = Path.of("uploads", "avatars");

            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = "/uploads/avatars/" + fileName;
            user.setAvatarUrl(avatarUrl);

            User savedUser = userRepository.save(user);

            return UserProfileResponseDto.builder()
                    .userId(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .fullName(savedUser.getFullName())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole().getName().name())
                    .avatarUrl(savedUser.getAvatarUrl())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }
}