package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.dailyjoke.DailyJokeService;
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
public class DailyJokeJob {

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final DailyJokeService dailyJokeService;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyJokeEmails() {
        log.debug("Job to send daily joke emails started");

        List<User> systemUsersList = userRepository.findAll();

        String dailyJoke = dailyJokeService.getRandomJoke();

        for (User user : systemUsersList) {
            try {
                emailService.sendDailyJokeEmail(user.getEmail(), user.getFirstName(), dailyJoke, user.isNotificationsEnabled());
            } catch (Exception e) {
                log.error("Error sending daily joke email to user: {}", user.getEmail());
            }
        }

        log.debug("Job to send daily joke emails finished");
    }

}
