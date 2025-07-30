package com.example.backend.service.impl;

import com.example.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        String subject = "Shohoj Krishi - Email Verification OTP";
        String body = String.format(
                "Dear User,\n\n" +
                        "Your OTP for email verification is: %s\n\n" +
                        "This OTP will expire in 5 minutes.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Shohoj Krishi Team",
                otp
        );

        sendSimpleEmail(to, subject, body);
    }

    @Override
    public void sendPasswordEmail(String to, String password, String fullName) {
        String subject = "Shohoj Krishi - Account Approved";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Congratulations! Your account has been approved.\n\n" +
                        "Your temporary password is: %s\n\n" +
                        "Please login and change your password immediately for security reasons.\n\n" +
                        "Best regards,\n" +
                        "Shohoj Krishi Team",
                fullName,
                password
        );

        sendSimpleEmail(to, subject, body);
    }
    @Override
    public void sendAcceptationEmail(String to, String fullName) {
        String subject = "Shohoj Krishi - Account Registration Update";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Congratulations!\n\n" +
                        "We are happy to inform you that your account registration has been approved.\n\n" +
                        "Please use your password to log in\n\n" +
                        "Best regards,\n" +
                        "Shohoj Krishi Team",
                fullName
        );

        sendSimpleEmail(to, subject, body);
    }

    @Override
    public void sendRejectionEmail(String to, String fullName, String reason) {
        String subject = "Shohoj Krishi - Account Registration Update";
        String body = String.format(
                "Dear %s,\n\n" +
                        "We regret to inform you that your account registration has not been approved.\n\n" +
                        "Reason: %s\n\n" +
                        "You may register again with correct information.\n\n" +
                        "Best regards,\n" +
                        "Shohoj Krishi Team",
                fullName,
                reason != null ? reason : "Does not meet our requirements"
        );

        sendSimpleEmail(to, subject, body);
    }

    private void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            // Don't throw exception to avoid breaking the flow
        }
    }
}