package com.library.management.service.user;

import com.library.management.dto.user.UpdateProfileRequestDto;
import com.library.management.dto.user.UserProfileResponseDto;
import com.library.management.entity.User;
import com.library.management.exception.InvalidRequestException;
import com.library.management.repository.UserRepository;
import com.library.management.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

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