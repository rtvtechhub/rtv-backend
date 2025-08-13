package com.rtvnewsnetwork.quiz.model;

import com.rtvnewsnetwork.config.model.UploadedFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsWithUsername {
    private String userId;
    private String userName;
    private int totalCorrectAnswers;
    private int totalQuestions;
    private int rank;
    private UploadedFile.RelativePath profileImageUrl;

}