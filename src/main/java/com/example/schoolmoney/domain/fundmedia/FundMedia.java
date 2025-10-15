package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.domain.fund.Fund;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @Column(name = "media_id")
    private UUID mediaId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_media_fund_id"))
    private Fund fund;

    @NotBlank
    @Size(max = 2_048)
    @Column(name = "media_url", nullable = false, updatable = false, length = 2_048)
    private String mediaUrl;

}
