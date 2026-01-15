package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.dailyjoke.JokeService;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.Getter;
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

    private final JokeService jokeService;

    @Getter
    private boolean isJobActive = true;

    public JobStatus setJobStatus(JobStatus status) {
        this.isJobActive = status == JobStatus.ON;

        log.info("Daily joke job status: {}", getJobStatus());

        return status;
    }

    public JobStatus getJobStatus() {
        return isJobActive ? JobStatus.ON : JobStatus.OFF;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void run() {
        if (!isJobActive) {
            return;
        }

        log.debug("Job to send daily joke emails started");

        List<User> systemUsersList = userRepository.findAll();

        String dailyJoke = jokeService.getRandomJoke();

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
