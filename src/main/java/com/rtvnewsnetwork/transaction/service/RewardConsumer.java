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
    //private final NotificationService notificationService;

    public RewardConsumer(UserRepository userRepository,
                          TransactionServiceImpl transactionService)
                   //       NotificationService notificationService)
    {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
      //  this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${event.kafka.topic}",
            groupId = "${interkashi.prod.kafka.groupid}"
    )
    public void processEvent(EventModel eventModel) {
        EventType eventType = eventModel.getEventType();
        String userId = eventModel.getUserId();

        // Filtering logic for Reward events
        if (eventType != null &&
                eventType != EventType.NEW_POST &&
                eventType != EventType.USER_PROFILE_COMPLETE_NOTIFICATION &&
                eventType != EventType.COIN_DEBIT &&
                eventType != EventType.COIN_CREDIT &&
                eventType != EventType.OTHERS) {

            if (userId != null) {
                TransactionModel transactionModel = transactionService.createTransactionData(eventType, userId);

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
                throw new IllegalArgumentException("EventType or userId cannot be null");
            }
        } else {
            System.out.println("Event not related to rewards, ignoring: " + eventType);
        }
    }
}
