package com.rtvnewsnetwork.notification.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationModel {

    @Id
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String logoUrl;
    private String path;

    @Builder.Default
    private List<String> userList = Collections.emptyList();

    @CreatedDate
    @Builder.Default
    private Instant createdDate = Instant.now();

    public NotificationModel(String title, String description, String imageUrl, String logoUrl, String path, Comparable<? extends Comparable<?>> comparable) {
    }
}

