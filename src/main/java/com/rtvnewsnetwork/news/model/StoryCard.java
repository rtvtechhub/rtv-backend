package com.rtvnewsnetwork.news.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryCard {
    private String imageUrl;
    private String title;
    private String description;
    private String shareUrl;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}