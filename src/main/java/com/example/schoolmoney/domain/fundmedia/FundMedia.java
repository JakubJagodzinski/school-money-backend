package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.files.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "fund_media")
public class FundMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fund_media_id")
    private UUID fundMediaId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_media_fund_id"))
    private Fund fund;

    @NotNull
    @Column(name = "file_id", nullable = false, updatable = false)
    private UUID fileId;

    @NotNull
    @Min(0)
    @Column(name = "file_size", nullable = false, updatable = false)
    private long fileSize;

    @NotBlank
    @Column(name = "content_type", nullable = false, updatable = false)
    private String contentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private FileType mediaType;

    @NotBlank
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_media_uploaded_by"))
    private Parent uploadedBy;

    @NotNull
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.uploadedAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

}
