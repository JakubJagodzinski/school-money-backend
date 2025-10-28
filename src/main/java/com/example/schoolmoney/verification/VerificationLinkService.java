package com.example.schoolmoney.verification;

import com.example.schoolmoney.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class VerificationLinkService {

    private final ServerProperties serverProperties;

    public String buildAccountVerificationLink(String verificationToken) {
        String tokenEncoded = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);

        return serverProperties.getPublicAddress() + "/api/v1/auth/verify?token=" + tokenEncoded;
    }

    public String buildChangeEmailConfirmationLink(String verificationToken) {
        String tokenEncoded = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);

        return serverProperties.getPublicAddress() + "/api/v1/users/email/change/confirm?token=" + tokenEncoded;
    }

    public String buildResetPasswordLink(String verificationToken) {
        String tokenEncoded = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);

        return serverProperties.getPublicAddress() + "/api/v1/password-reset?token=" + tokenEncoded;
    }

}
