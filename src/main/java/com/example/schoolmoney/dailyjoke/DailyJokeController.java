package com.example.schoolmoney.dailyjoke;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.dailyjoke.dto.response.DailyJokeJobStatusResponseDto;
import com.example.schoolmoney.dailyjoke.dto.response.RandomJokeResponseDto;
import com.example.schoolmoney.scheduler.DailyJokeJob;
import com.example.schoolmoney.scheduler.JobStatus;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DailyJokeController {

    private final JokeService jokeService;

    private final DailyJokeJob dailyJokeJob;

    @CheckPermission(Permission.RANDOM_JOKE_READ)
    @GetMapping("/joke")
    public ResponseEntity<RandomJokeResponseDto> getRandomJoke() {
        String joke = jokeService.getRandomJoke();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(RandomJokeResponseDto.builder().joke(joke).build());
    }

    @CheckPermission(Permission.DAILY_JOKE_TEST)
    @PostMapping("/daily-joke/test")
    public ResponseEntity<Void> testDailyJokeEvent() {
        dailyJokeJob.run();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @CheckPermission(Permission.DAILY_JOKE_TURN_OFF)
    @PostMapping("/daily-joke/status/off")
    public ResponseEntity<DailyJokeJobStatusResponseDto> turnOffDailyJokeEvent() {
        JobStatus status = dailyJokeJob.setJobStatus(JobStatus.OFF);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DailyJokeJobStatusResponseDto.builder()
                        .status(status)
                        .build());
    }

    @CheckPermission(Permission.DAILY_JOKE_TURN_ON)
    @PostMapping("/daily-joke/status/on")
    public ResponseEntity<DailyJokeJobStatusResponseDto> turnOnDailyJokeEvent() {
        JobStatus status = dailyJokeJob.setJobStatus(JobStatus.ON);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DailyJokeJobStatusResponseDto.builder()
                        .status(status)
                        .build());
    }

    @GetMapping("/daily-joke/status")
    public ResponseEntity<DailyJokeJobStatusResponseDto> getDailyJokeEventStatus() {
        JobStatus status = dailyJokeJob.getJobStatus();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DailyJokeJobStatusResponseDto.builder()
                        .status(status)
                        .build());
    }

}
