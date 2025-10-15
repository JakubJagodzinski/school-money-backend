package com.example.schoolmoney.logger;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_logs")
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @NotNull
    @Builder.Default
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    @Column(name = "method")
    private String method;

    @Column(name = "uri")
    private String uri;

    @Column(name = "query_string")
    private String queryString;

    @Column(name = "status")
    private String status;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "type")
    private String type;

}
