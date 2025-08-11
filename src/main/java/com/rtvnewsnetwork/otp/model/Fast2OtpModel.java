package com.rtvnewsnetwork.otp.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document("otp_requests_fast2")
public class Fast2OtpModel {

    @Id
    private String id;

    private String phoneNumber;
    private String otp;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant expiresAt = Instant.now().plusSeconds(300);

    @Builder.Default
    private Instant lastSentAt = Instant.now();

    @Builder.Default
    private boolean isUsed = false;

    @Builder.Default
    private int failedAttempts = 0;
}
