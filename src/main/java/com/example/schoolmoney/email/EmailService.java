package com.example.schoolmoney.email;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.email.contentproviders.account.*;
import com.example.schoolmoney.email.contentproviders.child.ChildAddedToClassEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.child.ChildReportEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.fund.*;
import com.example.schoolmoney.email.contentproviders.schoolclass.SchoolClassReportEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.wallet.WalletTopUpEmailContentProvider;
import com.example.schoolmoney.email.contentproviders.wallet.WalletWithdrawalEmailContentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Currency;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final AsyncEmailSender asyncEmailSender;

    private void sendEmail(String to, String firstName, EmailContentProvider emailContentProvider, boolean userNotificationsEnabled, byte[] attachmentBytes, String attachmentFileName) {
        if (emailContentProvider.isCritical() || userNotificationsEnabled) {
            asyncEmailSender.sendEmail(to, firstName, emailContentProvider, attachmentBytes, attachmentFileName);
        } else {
            log.info("Notifications are muted, skipping email ({}) to {}", emailContentProvider.getClass().getName(), to);
        }
    }

    private void sendEmail(String to, String firstName, EmailContentProvider emailContentProvider, boolean userNotificationsEnabled) {
        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled, null, null);
    }

    public void sendVerificationEmail(String to, String firstName, String verificationLink, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = AccountVerificationEmailContentProvider.builder()
                .verificationLink(verificationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendNewEmailConfirmationEmail(String to, String firstName, String newEmailConfirmationLink, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = NewEmailConfirmationEmailContentProvider.builder()
                .newEmailConfirmationLink(newEmailConfirmationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = PasswordResetEmailContentProvider.builder()
                .passwordResetUrl(resetPasswordRedirectUrl)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendAccountBlockedEmail(String to, String firstName, String reason, long durationInDays, Instant blockedUntil, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = AccountBlockedEmailContentProvider.builder()
                .reason(reason)
                .durationInDays(durationInDays)
                .blockedUntil(blockedUntil)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendAccountUnblockedEmail(String to, String firstName, String reason, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = AccountUnblockedEmailContentProvider.builder()
                .reason(reason)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendAccountBlockExpiredEmail(String to, String firstName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = AccountBlockExpiredEmailContentProvider.builder()
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendWalletWithdrawalEmail(String to, String firstName, long amountInCents, Currency currency, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = WalletWithdrawalEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents, Currency currency, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = WalletTopUpEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundReportEmail(String to, String firstName, String fundTitle, byte[] report, String reportTitle, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundReportEmailContentProvider.builder()
                .fundTitle(fundTitle)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled, report, reportTitle);
    }

    public void sendChildReportEmail(String to, String firstName, String childFullName, byte[] report, String reportTitle, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = ChildReportEmailContentProvider.builder()
                .childFullName(childFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled, report, reportTitle);
    }

    public void sendSchoolClassReportEmail(String to, String firstName, String schoolClassFullName, byte[] report, String reportTitle, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = SchoolClassReportEmailContentProvider.builder()
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled, report, reportTitle);
    }

    public void sendFundBlockedEmail(String to, String firstName, String fundName, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundBlockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundUnblockedEmail(String to, String firstName, String fundName, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundFinishedEmail(String to, String firstName, String fundName, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundCancelledEmail(String to, String firstName, String fundName, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundCancelledEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundPaymentRefundEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundPaymentRefundEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .childFullName(childFullName)
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundPaymentEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundPaymentEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .childFullName(childFullName)
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendChildAddedToClassEmail(String to, String firstName, String childFullName, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = ChildAddedToClassEmailContentProvider.builder()
                .childFullName(childFullName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

    public void sendFundCreatedEmail(String to, String firstName, String fundAuthorFullName, String fundTitle, String schoolClassFullName, boolean userNotificationsEnabled) {
        EmailContentProvider emailContentProvider = FundCreatedEmailContentProvider.builder()
                .fundAuthorFullName(fundAuthorFullName)
                .fundTitle(fundTitle)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider, userNotificationsEnabled);
    }

}
