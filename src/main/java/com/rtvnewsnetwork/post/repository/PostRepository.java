package com.rtvnewsnetwork.post.repository;

import com.rtvnewsnetwork.post.model.PostModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<PostModel, String> {
    @Override
    Page<PostModel> findAll(Pageable pageable);
}
