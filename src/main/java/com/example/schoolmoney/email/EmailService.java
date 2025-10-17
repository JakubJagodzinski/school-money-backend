package com.example.schoolmoney.email;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.email.contentproviders.RegistrationEmailContentProvider;
import com.example.schoolmoney.user.Role;
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

    private String generateVerificationLink(String verificationToken) {
        // TODO move address to configuration
        return "http://localhost:8090/api/v1/auth/verify?token=" + verificationToken;
    }

    @Async
    public void sendEmail(String to, String subject, EmailContentProvider contentProvider) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(contentProvider.build(), true);

        mailSender.send(message);
    }

    public void sendRegistrationEmail(String to, String firstName, Role role, String verificationToken) throws MessagingException {
        String verificationLink = generateVerificationLink(verificationToken);
        EmailContentProvider contentProvider = new RegistrationEmailContentProvider(firstName, role, verificationLink);
        sendEmail(to, "Complete your registration process", contentProvider);
    }

}
