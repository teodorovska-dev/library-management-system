package com.library.management.service.user;

import com.library.management.dto.user.UpdateProfileRequestDto;
import com.library.management.dto.user.UserProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserProfileResponseDto getCurrentUserProfile();

    UserProfileResponseDto updateCurrentUserProfile(UpdateProfileRequestDto requestDto);

    UserProfileResponseDto uploadCurrentUserAvatar(MultipartFile file);

    UserProfileResponseDto deleteCurrentUserAvatar();
}