package com.example.schoolmoney.email;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.email.contentproviders.*;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(
            String to,
            String subject,
            EmailContentProvider contentProvider,
            byte[] attachmentBytes,
            String attachmentFileName
    ) throws MailSendException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(contentProvider.build(), true);

            if (attachmentBytes != null && attachmentFileName != null) {
                helper.addAttachment(attachmentFileName, new ByteArrayResource(attachmentBytes));
            }

            mailSender.send(message);
        } catch (Exception e) {
            log.error(EmailMessages.FAILED_TO_SEND_EMAIL, e);
            throw new MailSendException(EmailMessages.FAILED_TO_SEND_EMAIL);
        }
    }

    public void sendVerificationEmail(String to, String firstName, String verificationLink) {
        EmailContentProvider emailContentProvider = new VerificationEmailContentProvider(firstName, verificationLink);
        sendEmail(to, "Verify your account", emailContentProvider, null, null);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents) {
        EmailContentProvider emailContentProvider = new WalletTopUpEmailContentProvider(firstName, amountInCents);
        sendEmail(to, "Wallet top-up", emailContentProvider, null, null);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl) {
        EmailContentProvider emailContentProvider = new ResetPasswordEmailContentProvider(firstName, resetPasswordRedirectUrl);
        sendEmail(to, "Reset your password", emailContentProvider, null, null);
    }

    public void sendFundReportEmail(String to, String firstName, String fundTitle, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = new FundReportEmailContentProvider(firstName, fundTitle);
        sendEmail(to, "Fund report", emailContentProvider, report, reportTitle);
    }

    public void sendWalletWithdrawalEmail(String to, String firstName, long amountInCents) {
        EmailContentProvider emailContentProvider = new WalletWithdrawalEmailContentProvider(firstName, amountInCents);
        sendEmail(to, "Wallet withdrawal", emailContentProvider, null, null);
    }

    public void sendAccountBlockedEmail(String to, String firstName, String reason, long durationInDays, Instant blockedUntil) {
        EmailContentProvider emailContentProvider = new AccountBlockedEmailContentProvider(firstName, reason, durationInDays, blockedUntil);
        sendEmail(to, "Account blocked", emailContentProvider, null, null);
    }

    public void sendAccountUnblockedEmail(String to, String firstName, String reason) {
        EmailContentProvider emailContentProvider = new AccountUnblockedEmailContentProvider(firstName, reason);
        sendEmail(to, "Account unblocked", emailContentProvider, null, null);
    }

    public void sendAccountBlockExpiredEmail(String to, String firstName) {
        EmailContentProvider emailContentProvider = new AccountBlockExpiredEmailContentProvider(firstName);
        sendEmail(to, "Account block expired", emailContentProvider, null, null);
    }

}
