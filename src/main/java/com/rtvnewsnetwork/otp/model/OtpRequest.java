package com.rtvnewsnetwork.otp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class OtpRequest {
    @Id
    private String id;
    private String phoneNumber;
    private String orderId;
    private String appHash;
    private int count = 1;
    private int verifyCount = 0;
    private boolean active = true;
    private String message;
    private Instant createdAt = Instant.now();

    public void incrementCount() {
        this.count++;
    }
}
