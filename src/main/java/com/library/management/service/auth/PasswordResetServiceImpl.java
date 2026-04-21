package com.library.management.service.auth;

import com.library.management.entity.PasswordResetCode;
import com.library.management.entity.User;
import com.library.management.exception.InvalidRequestException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.PasswordResetCodeRepository;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int RESET_CODE_EXPIRATION_MINUTES = 10;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[^A-Za-z0-9].*");

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email was not found"));

        PasswordResetCode existingCode = passwordResetCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElse(null);

        if (existingCode != null && Boolean.FALSE.equals(existingCode.getUsed())) {
            existingCode.setUsed(true);
            passwordResetCodeRepository.save(existingCode);
        }

        String code = generateFourDigitCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .email(user.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(RESET_CODE_EXPIRATION_MINUTES))
                .used(false)
                .build();

        passwordResetCodeRepository.save(resetCode);

        // Тимчасово: замість реальної відправки email
        System.out.println("Password reset code for " + user.getEmail() + ": " + code);
    }

    @Override
    public void verifyResetCode(String email, String code) {
        PasswordResetCode resetCode = passwordResetCodeRepository
                .findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new InvalidRequestException("Invalid verification code"));

        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Verification code has expired");
        }
    }

    @Override
    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetCode resetCode = passwordResetCodeRepository
                .findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new InvalidRequestException("Invalid verification code"));

        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Verification code has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email was not found"));

        validateNewPassword(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidRequestException("New password must be different from the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetCode.setUsed(true);
        passwordResetCodeRepository.save(resetCode);
    }

    private String generateFourDigitCode() {
        int value = 1000 + new Random().nextInt(9000);
        return String.valueOf(value);
    }

    private void validateNewPassword(String password) {
        if (password.length() < 8) {
            throw new InvalidRequestException("Password must contain at least 8 characters");
        }

        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            throw new InvalidRequestException("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            throw new InvalidRequestException("Password must contain at least one lowercase letter");
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            throw new InvalidRequestException("Password must contain at least one digit");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            throw new InvalidRequestException("Password must contain at least one special character");
        }
    }
}