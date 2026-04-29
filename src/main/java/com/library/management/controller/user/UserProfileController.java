package com.library.management.controller.user;

import com.library.management.dto.user.UpdateProfileRequestDto;
import com.library.management.dto.user.UserProfileResponseDto;
import com.library.management.service.user.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponseDto getCurrentUserProfile() {
        return userProfileService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserProfileResponseDto updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequestDto requestDto
    ) {
        return userProfileService.updateCurrentUserProfile(requestDto);
    }

    @DeleteMapping("/me/avatar")
    public UserProfileResponseDto deleteCurrentUserAvatar() {
        return userProfileService.deleteCurrentUserAvatar();
    }
}