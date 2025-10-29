package com.example.schoolmoney.email;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.email.contentproviders.account.*;
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

    public void sendEmail(String to, String firstName, EmailContentProvider emailContentProvider, byte[] attachmentBytes, String attachmentFileName) {
        asyncEmailSender.sendEmail(to, firstName, emailContentProvider, attachmentBytes, attachmentFileName);
    }

    public void sendEmail(String to, String firstName, EmailContentProvider emailContentProvider) {
        asyncEmailSender.sendEmail(to, firstName, emailContentProvider, null, null);
    }

    public void sendVerificationEmail(String to, String firstName, String verificationLink) {
        EmailContentProvider emailContentProvider = AccountVerificationEmailContentProvider.builder()
                .verificationLink(verificationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendNewEmailConfirmationEmail(String to, String firstName, String newEmailConfirmationLink) {
        EmailContentProvider emailContentProvider = ChangeEmailConfirmationEmailContentProvider.builder()
                .newEmailConfirmationLink(newEmailConfirmationLink)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetPasswordRedirectUrl) {
        EmailContentProvider emailContentProvider = PasswordResetEmailContentProvider.builder()
                .passwordResetUrl(resetPasswordRedirectUrl)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendAccountBlockedEmail(String to, String firstName, String reason, long durationInDays, Instant blockedUntil) {
        EmailContentProvider emailContentProvider = AccountBlockedEmailContentProvider.builder()
                .reason(reason)
                .durationInDays(durationInDays)
                .blockedUntil(blockedUntil)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendAccountUnblockedEmail(String to, String firstName, String reason) {
        EmailContentProvider emailContentProvider = AccountUnblockedEmailContentProvider.builder()
                .reason(reason)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendAccountBlockExpiredEmail(String to, String firstName) {
        EmailContentProvider emailContentProvider = AccountBlockExpiredEmailContentProvider.builder()
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendWalletWithdrawalEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = WalletWithdrawalEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendWalletTopUpEmail(String to, String firstName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = WalletTopUpEmailContentProvider.builder()
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider);
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

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendFundUnblockedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendFundFinishedEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundUnblockedEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendFundCancelledEmail(String to, String firstName, String fundName, String schoolClassFullName) {
        EmailContentProvider emailContentProvider = FundCancelledEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendFundPaymentRefundEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = FundPaymentRefundEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .childFullName(childFullName)
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

    public void sendFundPaymentEmail(String to, String firstName, String fundName, String schoolClassFullName, String childFullName, long amountInCents, Currency currency) {
        EmailContentProvider emailContentProvider = FundPaymentEmailContentProvider.builder()
                .fundTitle(fundName)
                .schoolClassFullName(schoolClassFullName)
                .childFullName(childFullName)
                .amountInCents(amountInCents)
                .currency(currency)
                .build();

        sendEmail(to, firstName, emailContentProvider);
    }

}
