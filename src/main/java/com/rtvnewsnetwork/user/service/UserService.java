package com.rtvnewsnetwork.user.service;

import com.rtvnewsnetwork.transaction.model.TransactionModel;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;

public interface UserService {
    User getUserById(String id);
    User findByUsernameElseCreate(String phoneNumber);

    User addUser(User user);
    User updateUser(String id, UserDto userDto);
    User findByPhoneNumber(String phoneNumber);
    public boolean updateUserWallet(TransactionModel transactionModel);
}
