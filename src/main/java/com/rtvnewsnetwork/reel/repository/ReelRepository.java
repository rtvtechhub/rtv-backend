package com.rtvnewsnetwork.reel.repository;

import com.rtvnewsnetwork.reel.model.ReelModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReelRepository extends MongoRepository<ReelModel, String> {
}
