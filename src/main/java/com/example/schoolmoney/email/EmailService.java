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
            String firstName,
            EmailContentProvider contentProvider,
            byte[] attachmentBytes,
            String attachmentFileName
    ) throws MailSendException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(contentProvider.getSubject());
            helper.setText(contentProvider.build(firstName), true);

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
        EmailContentProvider emailContentProvider = AccountVerificationEmailContentProvider.builder()
                .verificationLink(verificationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendNewEmailConfirmationEmail(String to, String firstName, String newEmailConfirmationLink) {
        EmailContentProvider emailContentProvider = ChangeEmailConfirmationEmailContentProvider.builder()
                .newEmailConfirmationLink(newEmailConfirmationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl) {
        EmailContentProvider emailContentProvider = PasswordResetEmailContentProvider.builder()
                .passwordResetUrl(resetPasswordRedirectUrl)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendAccountBlockedEmail(String to, String firstName, String reason, long durationInDays, Instant blockedUntil) {
        EmailContentProvider emailContentProvider = AccountBlockedEmailContentProvider.builder()
                .reason(reason)
                .durationInDays(durationInDays)
                .blockedUntil(blockedUntil)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendAccountUnblockedEmail(String to, String firstName, String reason) {
        EmailContentProvider emailContentProvider = AccountUnblockedEmailContentProvider.builder()
                .reason(reason)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendAccountBlockExpiredEmail(String to, String firstName) {
        EmailContentProvider emailContentProvider = AccountBlockExpiredEmailContentProvider.builder()
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendWalletWithdrawalEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = WalletWithdrawalEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = WalletTopUpEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendFundReportEmail(String to, String firstName, String fundTitle, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = FundReportEmailContentProvider.builder()
                .fundTitle(fundTitle)
                .build();

        sendEmail(to, firstName, emailContentProvider, report, reportTitle);
    }

    public void sendChildReportEmail(String to, String firstName, String childFullName, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = ChildReportEmailContentProvider.builder()
                .childFullName(childFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, report, reportTitle);
    }

    public void sendSchoolClassReportEmail(String to, String firstName, String schoolClassFullName, byte[] report, String reportTitle) {
        EmailContentProvider emailContentProvider = SchoolClassReportEmailContentProvider.builder()
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, report, reportTitle);
    }

    public void sendFundBlockedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundBlockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendFundUnblockedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendFundFinishedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendFundCancelledEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundCancelledEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendFundPaymentRefundEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = FundPaymentRefundEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .childFullName(childFullName)
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, null, null);
    }

}
