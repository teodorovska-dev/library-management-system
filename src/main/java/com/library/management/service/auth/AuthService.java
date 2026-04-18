package com.library.management.service.auth;

import com.library.management.dto.auth.*;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto requestDto);
    AuthResponseDto login(LoginRequestDto requestDto);
}