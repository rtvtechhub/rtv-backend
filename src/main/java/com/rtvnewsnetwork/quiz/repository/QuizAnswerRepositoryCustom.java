package com.rtvnewsnetwork.quiz.repository;

import com.rtvnewsnetwork.quiz.model.UserStatsWithUsernameDto;

import java.util.Date;
import java.util.List;
public interface QuizAnswerRepositoryCustom {

    List<UserStatsWithUsernameDto> findTopUsers(int limit, Date startDate, Date endDate);

    UserStatsWithUsernameDto findUserRankById(String userId, Date startDate, Date endDate);
    Long countUniqueUsers(Date startDate, Date endDate);
}

