package com.rtvnewsnetwork.comment.model;

import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentModel {

    @Id
    private String id;

    private String userId;
    private String username;
    private String comment;

    private List<CommentModel> replies = List.of();
    private List<TaggedUser> taggedUsers = List.of();
    private String contentId; // can be postId or reelId
    private CONTENT_TYPE targetType; // "POST" or "REEL"

    @CreatedDate
    private Instant createdAt;
}

