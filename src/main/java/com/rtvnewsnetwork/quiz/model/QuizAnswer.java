package com.rtvnewsnetwork.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class QuizAnswer {
    @Id
    @JsonIgnore
    private String id;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String quizId;

    private int correctAnswers;
    private int totalQuestions;
    private int totalCoinsEarned;
    private boolean showCoinWonScreen;
    private String successText;
    private Map<String, String> selectedAnswers;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}