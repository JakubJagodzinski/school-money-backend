package com.example.schoolmoney.verification;

import com.example.schoolmoney.common.constants.messages.VerificationTokenMessages;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.utils.RandomBase64TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final VerificationTokenProperties verificationTokenProperties;

    private void markExistingTokensAsUsed(User user, TokenType tokenType) {
        List<VerificationToken> tokens = verificationTokenRepository.findAllByUser_UserIdAndTokenTypeAndUsedFalse(user.getUserId(), tokenType);

        for (VerificationToken token : tokens) {
            token.setUsed(true);
        }

        verificationTokenRepository.saveAll(tokens);
    }

    public VerificationToken validateToken(String token) throws EntityNotFoundException {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
                    return new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
                });

        if (verificationToken.getExpiresAt().isBefore(Instant.now()) || verificationToken.isUsed()) {
            log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
            throw new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
        }

        return verificationToken;
    }

    @Transactional
    public String createVerificationToken(User user, TokenType tokenType) {
        markExistingTokensAsUsed(user, tokenType);

        VerificationToken token = VerificationToken
                .builder()
                .user(user)
                .token(RandomBase64TokenGenerator.generate(verificationTokenProperties.getTokenLength()))
                .expiresAt(Instant.now().plus(verificationTokenProperties.getExpiryHours(), ChronoUnit.HOURS))
                .tokenType(tokenType)
                .build();

        VerificationToken savedToken = verificationTokenRepository.save(token);

        return savedToken.getToken();
    }

}
