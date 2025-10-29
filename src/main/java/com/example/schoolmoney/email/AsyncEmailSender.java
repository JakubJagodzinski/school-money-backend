package com.example.schoolmoney.email;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncEmailSender {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String firstName, EmailContentProvider contentProvider, byte[] attachmentBytes, String attachmentFileName) throws MailSendException {
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

}
