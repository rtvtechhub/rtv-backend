package com.rtvnewsnetwork.notification.repository;

import com.rtvnewsnetwork.notification.model.FcmModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends MongoRepository<FcmModel, String> {


    FcmModel deleteByToken(String token);

    FcmModel findByDeviceId(String deviceId);
}
