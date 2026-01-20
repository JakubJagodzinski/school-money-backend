package com.example.schoolmoney.dailyjoke;

import com.example.schoolmoney.dailyjoke.dto.response.JokeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class JokeService {

    private final RestClient restClient;

    public JokeService(RestClient.Builder builder, JokeProperties jokeProperties) {
        this.restClient = builder
                .baseUrl(jokeProperties.getUrl())
                .build();
        log.info("Daily joke service initialized with url: {}", jokeProperties.getUrl());
    }

    public String getRandomJoke() {
        try {
            JokeResponseDto response = restClient.get()
                    .retrieve()
                    .body(JokeResponseDto.class);

            return mapJoke(response);
        } catch (Exception e) {
            log.error("Failed to fetch joke", e);
            return "Failed to fetch joke";
        }
    }

    private String mapJoke(JokeResponseDto response) {
        if (response == null) {
            return "Failed to fetch joke";
        }
        return response.getSetup() + " — " + response.getPunchline();
    }

}
