package com.example.schoolmoney.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VerificationLinkService {

    @Value("${SERVER_PORT}")
    private String serverPort;

    public String buildLink(String verificationToken) {
        String tokenEncoded = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);

        return "http://localhost:" + serverPort + "/api/v1/auth/verify?token=" + tokenEncoded;
    }

}
