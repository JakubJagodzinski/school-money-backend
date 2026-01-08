package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class WeekendJob {

    private final EmailService emailService;

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 16 ? * FRI")
    public void sendWeekendEmails() {
        log.debug("Job to send weekend emails started");

        List<User> systemUsersList = userRepository.findAll();

        for (User user : systemUsersList) {
            emailService.sendWeekendEmail(user.getEmail(), user.getFirstName(), user.isNotificationsEnabled());
        }

        log.debug("Job to send weekend emails finished");
    }

}
