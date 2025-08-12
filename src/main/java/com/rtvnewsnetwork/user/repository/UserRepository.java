package com.rtvnewsnetwork.user.repository;

import com.rtvnewsnetwork.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>,WalletRepository  {
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findAllByIdIn(List<String> userIds);}
