package com.rtvnewsnetwork.quiz.service;

import com.rtvnewsnetwork.quiz.model.*;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface QuizService {

    QuizAnswer calculateQuizAnswers(String userId, QuizModel quizModel, String questionId, String optionId);

    List<QuizModel> findQuiz();

    List<QuizFeed> getQuizFeed(String userId, Pageable pageable);

    QuizFeed submitQuiz(String userId, QuizSubmissionRequest quizSubmissionRequest);

    QuizFeed getQuizById(String userId, String quizId);

    Map<String, Object> getTopUsers(int limit, String userId, String filterType);

    Map.Entry<FilterEnum, Map.Entry<Date, Date>> getFilterEnumAndDateRange(String filterType);

    QuizModel createQuiz(QuizModel quizModel);

    QuizModel deleteQuiz(String id);
    void deleteQuizzes(List<String> quizIds);
}
