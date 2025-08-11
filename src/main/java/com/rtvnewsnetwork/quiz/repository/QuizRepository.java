package com.rtvnewsnetwork.quiz.repository;


import com.rtvnewsnetwork.quiz.model.QuizFeed;
import com.rtvnewsnetwork.quiz.model.QuizModel;
import com.rtvnewsnetwork.quiz.model.QuizStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<QuizModel, String> {

    @Aggregation(pipeline = {
            "{ $match: { status: 'ACTIVE' } }",
            "{ $project: { _quizId: { $toString: '$_id' }, questions: 1, status: 1, rewardCoinsPerQuestion: 1, share: 1, shareUrl: 1, createdAt: 1, updatedAt: 1 } }",
            "{ $lookup: { from: 'quizAnswer', localField: '_quizId', foreignField: 'quizId', as: 'userAnswers' } }",
            "{ $addFields: { userAnswers: { $filter: { input: '$userAnswers', as: 'a', cond: { $eq: ['$$a.userId', ?0] } } } } }",
            "{ $addFields: { answer: { $cond: { if: { $gt: [ { $size: '$userAnswers' }, 0 ] }, then: { $arrayElemAt: [ '$userAnswers', 0 ] }, else: null } } } }",
            "{ $project: { userAnswers: 0 } }"
    })
    List<QuizFeed> findQuiz(String userId, Pageable pageable);

    List<QuizModel> findAllByStatus(QuizStatus status, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?1) } }",
            "{ $project: { _quizId: { $toString: '$_id' }, questions: 1, status: 1, rewardCoinsPerQuestion: 1, share: 1, shareUrl: 1, createdAt: 1, updatedAt: 1 } }",
            "{ $lookup: { from: 'quizAnswer', localField: '_quizId', foreignField: 'quizId', as: 'answer' } }",
            "{ $unwind: { path: '$answer', preserveNullAndEmptyArrays: true } }",
            "{ $match: { 'answer.userId': ?0 } }"
    })
    QuizFeed findQuizById(String userId, String quizId);
}

