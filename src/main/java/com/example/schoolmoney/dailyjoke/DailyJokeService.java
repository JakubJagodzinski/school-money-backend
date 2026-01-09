package com.example.schoolmoney.dailyjoke;

import com.example.schoolmoney.dailyjoke.dto.response.JokeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class DailyJokeService {

    private final WebClient webClient;

    public DailyJokeService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://v2.jokeapi.dev/joke/Any")
                .build();
    }

    public String getRandomJoke() {
        return webClient.get()
                .retrieve()
                .bodyToMono(JokeResponse.class)
                .map(this::mapJoke)
                .onErrorReturn("failed to fetch joke")
                .block();
    }

    private String mapJoke(JokeResponse response) {
        if (response == null || response.isError()) {
            return "failed to fetch joke";
        }

        return "single".equals(response.getType())
                ? response.getJoke()
                : response.getSetup() + " — " + response.getDelivery();
    }

}
