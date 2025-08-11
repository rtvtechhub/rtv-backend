package com.rtvnewsnetwork.comment.repository;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<CommentModel, String> {
    List<CommentModel> findByContentId(String postId, Pageable pageable);

    List<CommentModel> findByContentIdAndTargetType(String reelId, CONTENT_TYPE contentType, Pageable pageable);
}

