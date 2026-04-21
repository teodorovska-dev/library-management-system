package com.library.management.controller.auth;

import com.library.management.dto.auth.*;
import com.library.management.service.auth.AuthService;
import com.library.management.service.auth.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDto> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto requestDto
    ) {
        passwordResetService.sendResetCode(requestDto.getEmail());

        return ResponseEntity.ok(
                MessageResponseDto.builder()
                        .message("If an account with that email exists, a verification code has been sent")
                        .build()
        );
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<MessageResponseDto> verifyResetCode(
            @Valid @RequestBody VerifyResetCodeRequestDto requestDto
    ) {
        passwordResetService.verifyResetCode(requestDto.getEmail(), requestDto.getCode());

        return ResponseEntity.ok(
                MessageResponseDto.builder()
                        .message("Verification code is valid")
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDto> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto requestDto
    ) {
        passwordResetService.resetPassword(
                requestDto.getEmail(),
                requestDto.getCode(),
                requestDto.getNewPassword()
        );

        return ResponseEntity.ok(
                MessageResponseDto.builder()
                        .message("Password has been reset successfully")
                        .build()
        );
    }
}