package com.rtvnewsnetwork.like.repository;

import com.rtvnewsnetwork.like.model.LikeModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<LikeModel, String> {
    int deleteByUserIdAndContentId(String userId, String postId);
    boolean existsByUserIdAndContentId(String userId, String postId);
}
