package com.library.management.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
}