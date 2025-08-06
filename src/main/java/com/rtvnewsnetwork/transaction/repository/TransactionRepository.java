package com.rtvnewsnetwork.transaction.repository;

import com.rtvnewsnetwork.transaction.model.TransactionModel;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionModel, String> {

//    @Aggregation(pipeline = {
//            // Step 1: Match documents within the specified date range
//            "{ $match: { createdAt: { $gte: { $date: ?0 }, $lte: { $date: ?1 } } } }",
//            // Step 2: Group by userId, calculate total earned amount, and find the earliest transaction time
//            "{ $group: { _id: \"$userId\", totalEarned: { $sum: { $cond: [ { $eq: [ \"$transactionType\", \"CREDIT\" ] }, \"$amount\", { $multiply: [\"$amount\", -1] } ] } }, earliestTime: { $min: \"$createdAt\" } } }",
//            // Step 3: Sort by total earned amount and earliest transaction time
//            "{ $sort: { totalEarned: -1, earliestTime: 1 } }",
//            // Step 4: Limit the results to the top 100 users
//            "{ $limit: 100 }",
//            // Step 5: Project the result into the UserCoinDto format
//            "{ $project: { userId: \"$_id\", amount: \"$totalEarned\", _id: 0 } }"
//    })
//    List<UserCoinDto> findTopUsersByEarnedCoins(String startDate, String endDate);
}
