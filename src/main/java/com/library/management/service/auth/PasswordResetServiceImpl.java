package com.library.management.service.auth;

import com.library.management.entity.PasswordResetCode;
import com.library.management.entity.User;
import com.library.management.exception.InvalidRequestException;
import com.library.management.repository.PasswordResetCodeRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[^A-Za-z0-9].*");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.password-reset.code-expiration-minutes}")
    private int codeExpirationMinutes;

    @Value("${app.password-reset.resend-cooldown-seconds}")
    private int resendCooldownSeconds;

    @Value("${app.password-reset.max-attempts}")
    private int maxAttempts;

    @Override
    public void sendResetCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        PasswordResetCode latestCode = passwordResetCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (latestCode != null
                && Boolean.FALSE.equals(latestCode.getUsed())
                && latestCode.getResendAvailableAt() != null
                && latestCode.getResendAvailableAt().isAfter(now)) {
            throw new InvalidRequestException("Please wait before requesting a new verification code");
        }

        if (latestCode != null && Boolean.FALSE.equals(latestCode.getUsed())) {
            latestCode.setUsed(true);
            passwordResetCodeRepository.save(latestCode);
        }

        String code = generateFourDigitCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .email(user.getEmail())
                .code(code)
                .expiresAt(now.plusMinutes(codeExpirationMinutes))
                .resendAvailableAt(now.plusSeconds(resendCooldownSeconds))
                .failedAttempts(0)
                .verified(false)
                .used(false)
                .build();

        passwordResetCodeRepository.save(resetCode);
        mailService.sendPasswordResetCode(user.getEmail(), code);
    }

    @Override
    public void verifyResetCode(String email, String code) {
        PasswordResetCode latestCode = passwordResetCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new InvalidRequestException("Invalid verification code"));

        validateCodeState(latestCode);

        if (!latestCode.getCode().equals(code)) {
            latestCode.setFailedAttempts(latestCode.getFailedAttempts() + 1);
            passwordResetCodeRepository.save(latestCode);

            if (latestCode.getFailedAttempts() >= maxAttempts) {
                throw new InvalidRequestException("Too many invalid attempts. Please request a new verification code");
            }

            throw new InvalidRequestException("Invalid verification code");
        }

        latestCode.setVerified(true);
        passwordResetCodeRepository.save(latestCode);
    }

    @Override
    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetCode resetCode = passwordResetCodeRepository
                .findByEmailAndCodeAndUsedFalseAndVerifiedTrue(email, code)
                .orElseThrow(() -> new InvalidRequestException("Verification code is invalid or not confirmed"));

        validateCodeState(resetCode);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("Invalid password reset request"));

        validateNewPassword(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidRequestException("New password must be different from the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetCode.setUsed(true);
        passwordResetCodeRepository.save(resetCode);
    }

    private void validateCodeState(PasswordResetCode resetCode) {
        LocalDateTime now = LocalDateTime.now();

        if (Boolean.TRUE.equals(resetCode.getUsed())) {
            throw new InvalidRequestException("Verification code has already been used");
        }

        if (resetCode.getExpiresAt().isBefore(now)) {
            throw new InvalidRequestException("Verification code has expired");
        }

        if (resetCode.getFailedAttempts() >= maxAttempts) {
            throw new InvalidRequestException("Too many invalid attempts. Please request a new verification code");
        }
    }

    private String generateFourDigitCode() {
        int value = 1000 + SECURE_RANDOM.nextInt(9000);
        return String.valueOf(value);
    }

    private void validateNewPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidRequestException("New password is required");
        }

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