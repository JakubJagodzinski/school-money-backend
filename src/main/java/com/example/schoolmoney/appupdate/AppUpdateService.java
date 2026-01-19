package com.example.schoolmoney.appupdate;

import com.example.schoolmoney.appupdate.dto.request.CreateAppUpdateRequestDto;
import com.example.schoolmoney.appupdate.dto.response.CurrentAppVersionResponseDto;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUpdateService {

    private final AppUpdateRepository appUpdateRepository;

    private final AppUpdateProperties appUpdateProperties;

    private final EmailService emailService;

    private final UserRepository userRepository;

    @Transactional
    public void saveAppUpdate(String secretKey, CreateAppUpdateRequestDto requestDto) {
        log.debug("Enter saveAppUpdate(newVersion={})", requestDto.getNewVersion());

        if (!secretKey.equals(appUpdateProperties.getSecretKey())) {
            log.warn("Invalid secret key");
            return;
        }

        AppUpdate appUpdate = AppUpdate.builder()
                .version(requestDto.getNewVersion())
                .changelog(requestDto.getChangelog())
                .build();

        appUpdateRepository.save(appUpdate);
        log.info("App update saved with version={}", appUpdate.getVersion());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        List<User> usersList = userRepository.findAll();

                        for (User user : usersList) {
                            emailService.sendNewApplicationVersionEmail(
                                    user.getEmail(),
                                    user.getFirstName(),
                                    appUpdate.getVersion(),
                                    appUpdate.getChangelog(),
                                    user.isNotificationsEnabled()
                            );
                        }

                    }
                }
        );

        log.debug("Exit saveAppUpdate(newVersion={})", requestDto.getNewVersion());
    }

    @Transactional(readOnly = true)
    public CurrentAppVersionResponseDto getCurrentAppVersion() {
        log.debug("Enter getCurrentAppVersion");

        AppUpdate appUpdate = appUpdateRepository.findFirstByOrderByVersionDesc()
                .orElse(null);
        if (appUpdate == null) {
            return null;
        }

        CurrentAppVersionResponseDto responseDto = CurrentAppVersionResponseDto.builder()
                .currentVersion(appUpdate.getVersion())
                .changelog(appUpdate.getChangelog())
                .build();

        log.debug("Exit getCurrentAppVersion");
        return responseDto;
    }

}
