package com.example.schoolmoney.usermoderation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class UserModerationEventController {

    private final UserModerationEventService userModerationEventService;

}
