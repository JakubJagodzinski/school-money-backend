package com.example.schoolmoney.requestlog;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AsyncRequestLogger {

    private final RequestLogRepository requestLogRepository;

    @Async
    public void logRequest(
            String method,
            String uri,
            String queryString,
            String clientIp,
            String userAgent,
            String status,
            UUID userId,
            Long durationMs,
            String traceId
    ) {
        RequestLog requestLog = RequestLog
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
                .build();

        requestLogRepository.save(requestLog);
    }

}
