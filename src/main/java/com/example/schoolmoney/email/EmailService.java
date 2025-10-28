package com.example.schoolmoney.email;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.email.contentproviders.account.*;
import com.example.schoolmoney.email.contentproviders.child.ChildReportEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.fund.*;
import com.example.schoolmoney.email.contentproviders.schoolclass.SchoolClassReportEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.wallet.WalletTopUpEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.wallet.WalletWithdrawalEmailContentProvider;
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
import java.util.Currency;

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
        EmailContentProvider emailContentProvider = new AccountVerificationEmailContentProvider(firstName, verificationLink);
        sendEmail(to, "Verify your account", emailContentProvider, null, null);
    }

    public void sendNewEmailConfirmationEmail(String to, String firstName, String verificationLink) {
        EmailContentProvider emailContentProvider = new ChangeEmailConfirmationEmailContentProvider(firstName, verificationLink);
        sendEmail(to, "Confirm your new email", emailContentProvider, null, null);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl) {
        EmailContentProvider emailContentProvider = new ResetPasswordEmailContentProvider(firstName, resetPasswordRedirectUrl);
        sendEmail(to, "Reset your password", emailContentProvider, null, null);
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

    public void sendWalletWithdrawalEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = new WalletWithdrawalEmailContentProvider(firstName, amountInCents, currency);
        sendEmail(to, "Wallet withdrawal", emailContentProvider, null, null);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = new WalletTopUpEmailContentProvider(firstName, amountInCents, currency);
        sendEmail(to, "Wallet top-up", emailContentProvider, null, null);
    }

    public void sendFundReportEmail(String to, String firstName, String fundTitle, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = new FundReportEmailContentProvider(firstName, fundTitle);
        sendEmail(to, "Fund report", emailContentProvider, report, reportTitle);
    }

    public void sendChildReportEmail(String to, String firstName, String childFullName, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = new ChildReportEmailContentProvider(firstName, childFullName);
        sendEmail(to, "Your child report", emailContentProvider, report, reportTitle);
    }

    public void sendSchoolClassReportEmail(String to, String firstName, String schoolClassFullName, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = new SchoolClassReportEmailContentProvider(firstName, schoolClassFullName);
        sendEmail(to, "School class report", emailContentProvider, report, reportTitle);
    }

    public void sendFundBlockedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = new FundBlockedEmailContentProvider(firstName, fundName, schoolClassFullName);
        sendEmail(to, "Fund blocked", emailContentProvider, null, null);
    }

    public void sendFundUnblockedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = new FundUnblockedEmailContentProvider(firstName, fundName, schoolClassFullName);
        sendEmail(to, "Fund unblocked", emailContentProvider, null, null);
    }

    public void sendFundFinishedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = new FundUnblockedEmailContentProvider(firstName, fundName, schoolClassFullName);
        sendEmail(to, "Fund finished", emailContentProvider, null, null);
    }

    public void sendFundCancelledEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = new FundCancelledEmailContentProvider(firstName, fundName, schoolClassFullName);
        sendEmail(to, "Fund cancelled", emailContentProvider, null, null);
    }

    public void sendFundPaymentRefundEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = new FundPaymentRefundEmailContentProvider(firstName, fundName, schoolClassFullName, childFullName, amountInCents, currency);
        sendEmail(to, "Fund payment refund", emailContentProvider, null, null);
    }

}
