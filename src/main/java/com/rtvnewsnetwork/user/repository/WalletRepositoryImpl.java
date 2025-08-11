package com.rtvnewsnetwork.user.repository;

import com.rtvnewsnetwork.transaction.model.TransactionType;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.rtvnewsnetwork.transaction.model.TransactionType.CREDIT;
import static com.rtvnewsnetwork.transaction.model.TransactionType.DEBIT;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public WalletRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Transactional
    public boolean updateWallet(String userId, TransactionType type, int amount) {
        try {
            System.out.println("Inside update wallet: " + amount + " " + userId + " " + type);
            Query query = new Query(Criteria.where("_id").is(userId));

            Update update;

            switch (type) {
                case CREDIT:
                    update = new Update()
                            .inc("userWallet.coinBalance", amount)
                            .inc("userWallet.totalCoinsEarned", amount);
                    break;

                case DEBIT:
                    UserWallet currentBalance = checkWalletBalance(userId);
                    System.out.println("Current balance: " + currentBalance);

                    if ((currentBalance != null ? currentBalance.getCoinBalance() : 0) >= amount) {
                        update = new Update()
                                .inc("userWallet.coinBalance", -amount)
                                .inc("userWallet.totalCoinsEarned", -amount);
                    } else {
                        System.out.println("Notifying user " + userId + " about insufficient balance");
                        return false; // Early return if balance is insufficient
                    }
                    break;

                default:
                    update = new Update(); // Fallback
                    break;
            }

            if (update != null && !update.getUpdateObject().isEmpty()) {
                var result = mongoTemplate.updateFirst(query, update, User.class);
                return result.getModifiedCount() > 0;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error occurred while updating wallet: " + e.getMessage());
            return false;
        }
    }

    @Override
    public UserWallet checkWalletBalance(String userId) {
        try {
            Query query = new Query(Criteria.where("_id").is(userId));
            User user = mongoTemplate.findOne(query, User.class);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            return user.getUserWallet();
        } catch (Exception e) {
            System.out.println("Error occurred while checking wallet balance: " + e.getMessage());
            throw e;
        }
    }
}

