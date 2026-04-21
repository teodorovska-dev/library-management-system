package com.library.management.service.mail;

public interface MailService {
    void sendPasswordResetCode(String toEmail, String code);
}