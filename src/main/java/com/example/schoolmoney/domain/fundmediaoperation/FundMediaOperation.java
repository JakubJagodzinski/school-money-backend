package com.example.schoolmoney.domain.fundmediaoperation;

import com.example.schoolmoney.files.FileType;
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
@Table(name = "fund_media_operations")
public class FundMediaOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "operation_id")
    private UUID operationId;

    @NotNull
    @Column(name = "performed_by", nullable = false, updatable = false)
    private UUID performedById;

    @NotNull
    @Column(name = "performed_by_full_name", nullable = false, updatable = false)
    private String performedByFullName;

    @NotNull
    @Column(name = "fund_media_id", nullable = false, updatable = false)
    private UUID fundMediaId;

    @NotNull
    @Column(name = "filename", nullable = false, updatable = false)
    private String filename;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, updatable = false)
    private FileType mediaType;

    @NotNull
    @Column(name = "fund_id", nullable = false, updatable = false)
    private UUID fundId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private FundMediaOperationType operationType;

    @NotNull
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt;

    @PrePersist
    protected void onCreate() {
        this.processedAt = Instant.now();
    }

}
