package com.rtvnewsnetwork.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsInsightModel {
    private int noOfComments = 0;
    private int noOfLikes = 0;
}
