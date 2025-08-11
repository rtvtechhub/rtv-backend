package com.rtvnewsnetwork.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quizModel")
public class QuizFeed extends QuizModel {
    private QuizAnswer answer;

    public QuizFeed(QuizModel quizModel, QuizAnswer answer) {
        super(
                quizModel.getId(),
                quizModel.getQuestions(),
                quizModel.getStatus(),
                quizModel.getRewardCoinsPerQuestion(),
                quizModel.getShare(),
                quizModel.getShareUrl() != null ? quizModel.getShareUrl() : "defaultShareUrl",
                quizModel.getCreatedAt(),
                quizModel.getUpdatedAt()
        );
        this.answer = answer;
    }
}
