package com.rtvnewsnetwork.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDto {
    private Integer totalQuestions = 0;
    private Integer totalCorrectAnswers = 0;
    private Double correctness = 0.0;
}