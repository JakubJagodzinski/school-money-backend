package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LongAbsenceJob {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Scheduled(cron = "0 0 14 * * *")
    public void sendLongAbsenceEmails() {
        log.debug("Job to send long absence emails started");

        Instant cutoffDate = Instant.now().minus(30, ChronoUnit.DAYS);

        List<User> longAbsentUsersList = userRepository.findAllByLastLoggedInBefore(cutoffDate);

        for (User user : longAbsentUsersList) {
            try {
                emailService.sendLongAbsenceEmail(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastLoggedIn(),
                        user.isNotificationsEnabled()
                );
            } catch (Exception e) {
                log.error("Error sending long absence email to user: {}", user.getEmail());
            }
        }

        log.info("Sent long absence emails to {} users", longAbsentUsersList.size());

        log.debug("Job to send long absence emails finished");
    }

}
