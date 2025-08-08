package com.rtvnewsnetwork.reel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class ReelModel {

    @Id
    private String id;
    private String title;
    private RelativePath thumbnail;
    private RelativePath videoURL;
    private ReelInsightModel insights;
    private ReviewStatus status;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
    @Transient
    @JsonProperty("liked")
    private boolean isLiked = false;

}
