package com.library.management.service.auth;

public interface PasswordResetService {

    void sendResetCode(String email);

    void verifyResetCode(String email, String code);

    void resetPassword(String email, String code, String newPassword);
}