package com.library.management.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.password-reset.from-email}")
    private String fromEmail;

    @Override
    public void sendPasswordResetCode(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Library Management System - Password Reset Code");
            helper.setText(buildPasswordResetHtml(code), true);

            javaMailSender.send(message);
        } catch (MessagingException | MailException ex) {
            throw new IllegalStateException("Failed to send password reset email");
        }
    }

    private String buildPasswordResetHtml(String code) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; background-color: #f7f7f5; color: #111111; padding: 24px;">
                    <div style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 12px; padding: 32px; border: 1px solid #e5e5e5;">
                      <h2 style="margin: 0 0 16px 0;">Password Reset Request</h2>
                      <p style="margin: 0 0 16px 0;">Use the verification code below to reset your password:</p>
                      <div style="font-size: 32px; font-weight: 700; letter-spacing: 8px; text-align: center; padding: 16px 0; background: #111111; color: #ffffff; border-radius: 10px; margin: 24px 0;">
                        %s
                      </div>
                      <p style="margin: 0 0 12px 0;">This code will expire in 10 minutes.</p>
                      <p style="margin: 0; color: #666666;">If you did not request a password reset, you can safely ignore this email.</p>
                    </div>
                  </body>
                </html>
                """.formatted(code);
    }
}