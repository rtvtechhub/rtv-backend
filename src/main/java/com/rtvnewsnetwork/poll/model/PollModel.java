package com.rtvnewsnetwork.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "pollModel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollModel {

    @Id
    private String id;

    private PollQuestion question;

    private ResponseStats responseStats = new ResponseStats();

    private Instant expiryDate;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private String shareUrl;
}
