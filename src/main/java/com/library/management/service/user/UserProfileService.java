package com.library.management.service.user;

import com.library.management.dto.user.UpdateProfileRequestDto;
import com.library.management.dto.user.UserProfileResponseDto;

public interface UserProfileService {

    UserProfileResponseDto getCurrentUserProfile();

    UserProfileResponseDto updateCurrentUserProfile(UpdateProfileRequestDto requestDto);
}