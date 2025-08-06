package com.rtvnewsnetwork.quiz.repository;

import com.rtvnewsnetwork.quiz.model.QuizAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends MongoRepository<QuizAnswer, String>, QuizAnswerRepositoryCustom {

    QuizAnswer findByUserIdAndQuizId(String userId, String quizId);

    void deleteByQuizId(String quizId);
}