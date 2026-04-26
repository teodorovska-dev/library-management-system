package com.library.management.service.user;

import com.library.management.dto.user.UserProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserAvatarService {

    UserProfileResponseDto uploadCurrentUserAvatar(MultipartFile file);
}