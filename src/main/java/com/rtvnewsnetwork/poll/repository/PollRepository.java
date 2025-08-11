package com.rtvnewsnetwork.poll.repository;

import com.rtvnewsnetwork.poll.model.AggregatedPollModel;
import com.rtvnewsnetwork.poll.model.PollModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PollRepository extends MongoRepository<PollModel, String> {

    // TODO: Test the performance of this aggregation
    @Aggregation(pipeline = {
            "{ $project: { _pollId: { $toString: \"$_id\" }, question: 1, responseStats: 1, expiryDate: 1, createdAt: 1, updatedAt: 1, shareUrl: 1 } }",
            "{ $lookup: { from: \"pollResponse\", localField: \"_pollId\", foreignField: \"pollId\", as: \"answer\" } }",
            "{ $project: { question: 1, responseStats: 1, expiryDate: 1, createdAt: 1, updatedAt: 1, shareUrl: 1, answer: { $filter: { input: \"$answer\", as: \"userAnswer\", cond: { $eq: [\"$$userAnswer.userId\", ?0] } } } } }",
            "{ $addFields: { responseCountZero: { $cond: { if: { $eq: [\"$responseStats.responseCount\", 0] }, then: 1, else: 0 } } } }",
            "{ $sort: { responseCountZero: -1, createdAt: -1 } }"
    })
    List<AggregatedPollModel> getAggregatedPoll(@Param("userId") String userId, PageRequest pageRequest);
}
