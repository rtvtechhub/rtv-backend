package com.rtvnewsnetwork.poll.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "pollModel")
public class AggregatedPollModel extends PollModel {

    private PollResponse[] answer;

    public AggregatedPollModel(
            String id,
            PollQuestion question,
            ResponseStats responseStats,
            Instant expiryDate,
            Instant createdAt,
            Instant updatedAt,
            String shareUrl,
            PollResponse[] answer
    ) {
        super(id, question, responseStats, expiryDate, createdAt, updatedAt, shareUrl);
        this.answer = answer;
    }
}
