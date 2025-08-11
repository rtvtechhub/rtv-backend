package com.rtvnewsnetwork.user.repository;

import com.rtvnewsnetwork.transaction.model.TransactionType;
import com.rtvnewsnetwork.user.model.UserWallet;

public interface WalletRepository {

    boolean updateWallet(String userId, TransactionType type, int amount);

    UserWallet checkWalletBalance(String userId);
}
