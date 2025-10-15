package com.example.schoolmoney.logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AsyncRequestLogger {

    private final RequestLogRepository requestLogRepository;

    public AsyncRequestLogger(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Async
    public void logRequest(String method,
                           String uri,
                           String queryString,
                           String clientIp,
                           String userAgent,
                           String status,
                           UUID userId,
                           Long durationMs,
                           String traceId,
                           String type) {

        RequestLog log = RequestLog
                .builder()
                .method(method)
                .uri(uri)
                .queryString(queryString)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .status(status)
                .userId(userId)
                .durationMs(durationMs)
                .traceId(traceId)
                .type(type)
                .build();

        requestLogRepository.save(log);
    }

}
