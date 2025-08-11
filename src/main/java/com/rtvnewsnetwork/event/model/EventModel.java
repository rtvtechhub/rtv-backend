package com.rtvnewsnetwork.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event")
public class EventModel {

    @Id
    private String eventId;

    private EventType eventType;

    private String userId;

    private Object data;  // Kotlin's Any? → Java's Object

    @CreatedDate
    private Instant createdAt;
}
