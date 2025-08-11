package com.rtvnewsnetwork.transaction.service;

import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.transaction.model.TransactionModel;

import java.util.List;

public interface TransactionService {

    TransactionModel createTransactionData(EventType eventType, String userId);

    void saveTransactionAndUpdateWallet(TransactionModel transactionModel);

  //  List<UserCoinDto> findTopFanBadge(String startDate, String endDate);
}
