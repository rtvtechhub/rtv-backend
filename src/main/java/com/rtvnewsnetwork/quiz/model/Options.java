package com.rtvnewsnetwork.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Options {
    private String id = new ObjectId().toString();
    private String label;
    private boolean isCorrectAnswer;

    @Transient
    private boolean isAnswered = false;
}