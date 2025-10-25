package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserExpiredBlockJob {

    private final UserService userService;

    @Scheduled(cron = "0 0 * * * *")
    public void unblockExpiredUsers() {
        log.debug("Job to unblock expired users started");

        userService.unblockUsersWithExpiredBlock();

        log.debug("Job to unblock expired users finished");
    }

}
