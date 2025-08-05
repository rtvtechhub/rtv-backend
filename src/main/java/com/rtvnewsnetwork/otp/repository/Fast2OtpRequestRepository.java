package com.rtvnewsnetwork.otp.repository;

import com.rtvnewsnetwork.otp.model.Fast2OtpModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Fast2OtpRequestRepository extends MongoRepository<Fast2OtpModel, String> {
     Fast2OtpModel findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}