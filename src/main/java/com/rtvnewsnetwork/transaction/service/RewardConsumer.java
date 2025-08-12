package com.rtvnewsnetwork.transaction.service;

import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.transaction.model.TransactionModel;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class RewardConsumer {

    private final UserRepository userRepository;
    private final TransactionServiceImpl transactionService;

    public RewardConsumer(UserRepository userRepository,
                          TransactionServiceImpl transactionService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "${event.kafka.topic.coinupdates}",
            groupId = "${rtv.dev.kafka.groupid.coinupdates}"
    )
    public void processEvent(EventModel eventModel) {
        String userId = eventModel.getUserId();

        if (userId != null) {
            TransactionModel transactionModel = transactionService.createTransactionData(
                    eventModel.getEventType(),
                    userId
            );

            Object data = eventModel.getData();
            int earnedCoin = 0;

            if (data instanceof java.util.Map<?, ?>) {
                Object coinsEarned = ((java.util.Map<?, ?>) data).get("coinsEarned");
                if (coinsEarned instanceof Integer) {
                    earnedCoin = (Integer) coinsEarned;
                }
            }

            if (transactionModel.getAmount() == 0) {
                transactionModel.setAmount(earnedCoin);
            }

            transactionService.saveTransactionAndUpdateWallet(transactionModel);

        } else {
            throw new IllegalArgumentException("userId cannot be null");
        }
    }
}
