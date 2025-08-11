package com.rtvnewsnetwork.news.model;


import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import com.rtvnewsnetwork.post.model.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class NewsModel {
    private String id=null;
    private String title=null;
    private String description=null;
    private RelativePath bannerImage=null;
    private Video video=null;
    private List<StoryCard> storyCards;
    private List<String> tags;
    private String shareUrl;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
