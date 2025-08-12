package com.rtvnewsnetwork.notification.repository;

import com.rtvnewsnetwork.notification.model.NotificationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationModel, String> {
}
