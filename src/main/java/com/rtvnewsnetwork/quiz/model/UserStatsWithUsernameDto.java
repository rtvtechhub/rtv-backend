package com.rtvnewsnetwork.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsWithUsernameDto {

    private String userId;
    private String userName;
    private int totalCorrectAnswers;
    private int totalQuestions;
    private int rank;


    //private RelativePath profileImageUrl; // Nullable

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId); // Will be null-safe
        map.put("userName", userName);
        map.put("totalCorrectAnswers", totalCorrectAnswers);
        map.put("totalQuestions", totalQuestions);
        map.put("rank", rank);
     //   map.put("profileImageUrl", profileImageUrl != null ? profileImageUrl : "");
        return map;
    }
}
