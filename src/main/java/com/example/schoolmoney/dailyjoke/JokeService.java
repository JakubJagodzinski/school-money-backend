package com.example.schoolmoney.dailyjoke;

import com.example.schoolmoney.dailyjoke.dto.response.JokeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class JokeService {

    private final WebClient webClient;

    public JokeService(WebClient.Builder builder, JokeProperties jokeProperties) {
        this.webClient = builder
                .baseUrl(jokeProperties.getUrl())
                .build();
        log.info("Daily joke service initialized with url: {}", jokeProperties.getUrl());
    }

    public String getRandomJoke() {
        return webClient.get()
                .retrieve()
                .bodyToMono(JokeResponseDto.class)
                .map(this::mapJoke)
                .onErrorReturn("Failed to fetch joke")
                .block();
    }

    private String mapJoke(JokeResponseDto response) {
        if (response == null) {
            return "Failed to fetch joke";
        }

        return response.getSetup() + " — " + response.getPunchline();
    }

}
