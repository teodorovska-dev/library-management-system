package com.library.management.controller.user;

import com.library.management.dto.user.UserProfileResponseDto;
import com.library.management.service.user.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me/avatar")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    @PostMapping
    public UserProfileResponseDto uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userAvatarService.uploadCurrentUserAvatar(file);
    }
}