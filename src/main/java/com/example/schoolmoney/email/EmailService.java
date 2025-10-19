package com.example.schoolmoney.email;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.email.contentproviders.ResetPasswordEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.VerificationEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.WalletTopUpEmailContentProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, EmailContentProvider contentProvider) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(contentProvider.build(), true);

        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String firstName, String verificationLink) throws MessagingException {
        EmailContentProvider emailContentProvider = new VerificationEmailContentProvider(firstName, verificationLink);
        sendEmail(to, "Verify your account", emailContentProvider);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents) throws MessagingException {
        EmailContentProvider emailContentProvider = new WalletTopUpEmailContentProvider(firstName, amountInCents);
        sendEmail(to, "Wallet top-up", emailContentProvider);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl) throws MessagingException {
        EmailContentProvider emailContentProvider = new ResetPasswordEmailContentProvider(firstName, resetPasswordRedirectUrl);
        sendEmail(to, "Reset your password", emailContentProvider);
    }

}
