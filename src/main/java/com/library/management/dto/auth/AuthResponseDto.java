package com.library.management.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}