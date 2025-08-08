package com.rtvnewsnetwork.post.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import com.rtvnewsnetwork.quiz.model.ShareModel;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PostModel {

    @Id
    private String id;

    private String title;
    private String description;
    private RelativePath bannerImage=null;
    private PostsInsightModel insights;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
    private Video video;

    @Transient
    @JsonProperty("liked")
    private boolean isLiked = false;
}

