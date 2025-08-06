package com.rtvnewsnetwork.event.repository;

import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.event.model.EventType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<EventModel, String> {

    boolean existsByUserIdAndEventType(String userId, EventType eventType);
}

