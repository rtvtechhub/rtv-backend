package com.rtvnewsnetwork.otp.repository;

import com.rtvnewsnetwork.otp.model.OtpRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRequestRepository extends MongoRepository<OtpRequest, String> {
    Optional<OtpRequest> findByPhoneNumberAndActive(String phoneNumber, Boolean active);
}
