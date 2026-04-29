package com.library.management.service.user;

import com.library.management.dto.user.UpdateProfileRequestDto;
import com.library.management.dto.user.UserProfileResponseDto;
import com.library.management.entity.User;
import com.library.management.exception.InvalidRequestException;
import com.library.management.repository.UserRepository;
import com.library.management.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Override
    public UserProfileResponseDto getCurrentUserProfile() {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        return mapToResponseDto(currentUser);
    }

    @Override
    public UserProfileResponseDto updateCurrentUserProfile(UpdateProfileRequestDto requestDto) {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();

        userRepository.findByEmail(requestDto.getEmail())
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .ifPresent(user -> {
                    throw new InvalidRequestException("Email is already used by another account");
                });

        currentUser.setFirstName(requestDto.getFirstName().trim());
        currentUser.setLastName(requestDto.getLastName().trim());
        currentUser.setFullName(requestDto.getFirstName().trim() + " " + requestDto.getLastName().trim());
        currentUser.setEmail(requestDto.getEmail().trim());

        User updatedUser = userRepository.save(currentUser);

        return mapToResponseDto(updatedUser);
    }

    @Override
    public UserProfileResponseDto uploadCurrentUserAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Avatar file is required");
        }

        User currentUser = currentUserService.getCurrentAuthenticatedUser();

        try {
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path avatarsPath = Paths.get(uploadDir, "avatars");

            Files.createDirectories(avatarsPath);

            Path targetPath = avatarsPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            currentUser.setAvatarUrl("/uploads/avatars/" + fileName);

            User updatedUser = userRepository.save(currentUser);
            return mapToResponseDto(updatedUser);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store user avatar", e);
        }
    }

    @Override
    public UserProfileResponseDto deleteCurrentUserAvatar() {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();

        currentUser.setAvatarUrl(null);

        User updatedUser = userRepository.save(currentUser);
        return mapToResponseDto(updatedUser);
    }

    private UserProfileResponseDto mapToResponseDto(User user) {
        return UserProfileResponseDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}