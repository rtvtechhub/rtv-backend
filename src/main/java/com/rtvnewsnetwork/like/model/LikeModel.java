package com.rtvnewsnetwork.like.model;

import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeModel {
    private String id;
    private String userId;
    private String contentId;
    private CONTENT_TYPE contentType;
}
