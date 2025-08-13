package com.rtvnewsnetwork.quiz.repository;

import com.mongodb.client.MongoClient;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import com.rtvnewsnetwork.quiz.model.UserStatsWithUsernameDto;
import com.rtvnewsnetwork.quiz.service.QuizServiceImpl;
import com.rtvnewsnetwork.user.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class QuizAnswerRepositoryCustomImpl implements QuizAnswerRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);

    @Autowired
    public QuizAnswerRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserStatsWithUsernameDto> findTopUsers(int limit, Date startDate, Date endDate) {
        var match1 = Aggregation.match(Criteria.where("userId").ne(null));

        var matchDate = Aggregation.match(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );

        var group = Aggregation.group("userId")
                .sum("correctAnswers").as("totalCorrectAnswers")
                .sum("totalQuestions").as("totalQuestions")
                .min("createdAt").as("firstPlayedDate");

        var sort = Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalCorrectAnswers")
                .and(org.springframework.data.domain.Sort.Direction.ASC, "firstPlayedDate");

        var limitAgg = Aggregation.limit(limit);

        var aggregation = Aggregation.newAggregation(match1, matchDate, group, sort, limitAgg);

        AggregationResults<Document> result =
                mongoTemplate.aggregate(aggregation, "quizAnswer", Document.class);
        List<Document> topUsersStats = result.getMappedResults();

        return IntStream.range(0, topUsersStats.size())
                .mapToObj(index -> {
                    Document stats = topUsersStats.get(index);
                    String userIdStr = stats.getString("_id") != null ? stats.getString("_id") : "Unknown";
                    logger.info("Processing userId: {}", userIdStr);

                    ObjectId objectId = null;
                    try {
                        objectId = new ObjectId(userIdStr);
                    } catch (IllegalArgumentException e) {
                        logger.error("Failed to convert userId {} to ObjectId", userIdStr, e);
                    }

                    if (objectId == null) {
                        logger.warn("Skipping userId {} due to invalid ObjectId", userIdStr);
                        return new UserStatsWithUsernameDto(
                                userIdStr,
                                "Unknown",
                                stats.getInteger("totalCorrectAnswers", 0),
                                stats.getInteger("totalQuestions", 0),
                                index + 1,
                                stats.get("profileImageUrl", null)
//                                null
                        );
                    }

                    User user = mongoTemplate.findById(objectId, User.class);
                    logger.info("Fetched user: {}", user);

                    String userName = (user != null && user.getName() != null) ? user.getName() : "Unknown";
                   // String profileImageUrl = (user != null) ? user.getProfileImageUrl() : null;

                    logger.info("User name resolved to: {}", userName);
                  //  logger.info("Profile Image URL resolved to: {}", profileImageUrl);

                    return new UserStatsWithUsernameDto(
                            userIdStr,
                            userName,
                            stats.getInteger("totalCorrectAnswers", 0),
                            stats.getInteger("totalQuestions", 0),
                            index + 1,
                            stats.get("profileImageUrl", null)
                     //       profileImageUrl
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserStatsWithUsernameDto findUserRankById(String userId, Date startDate, Date endDate) {
        if (userId == null) {
            logger.warn("UserId is null.");
            return null;
        }

        var match1 = Aggregation.match(Criteria.where("userId").ne(null));

        var matchDate = Aggregation.match(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );

        var group = Aggregation.group("userId")
                .sum("correctAnswers").as("totalCorrectAnswers")
                .sum("totalQuestions").as("totalQuestions")
                .min("createdAt").as("firstPlayedDate");

        var sort = Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalCorrectAnswers")
                .and(org.springframework.data.domain.Sort.Direction.ASC, "firstPlayedDate");

        var aggregation = Aggregation.newAggregation(match1, matchDate, group, sort);

        AggregationResults<Document> result =
                mongoTemplate.aggregate(aggregation, "quizAnswer", Document.class);
        List<Document> userStatsList = result.getMappedResults();

        Document userStats = userStatsList.stream()
                .filter(doc -> userId.equals(doc.getString("_id")))
                .findFirst()
                .orElse(null);

        if (userStats == null) {
            logger.warn("User with userId {} not found in the results.", userId);
            return null;
        }

        int rank = userStatsList.indexOf(userStats) + 1;

        ObjectId objectId;
        try {
            objectId = new ObjectId(userId);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to convert userId {} to ObjectId", userId, e);
            return new UserStatsWithUsernameDto(
                    userId,
                    "Unknown",
                    userStats.getInteger("totalCorrectAnswers", 0),
                    userStats.getInteger("totalQuestions", 0),
                    rank,
                    userStats.get("profileImageUrl", null)
//                    null
            );
        }

        User user = mongoTemplate.findById(objectId, User.class);
        String userName = (user != null && user.getName() != null) ? user.getName() : "Unknown";
        RelativePath profileImageUrl = (user != null) ? user.getProfileImage() : null;

        return new UserStatsWithUsernameDto(
                userId,
                userName,
                userStats.getInteger("totalCorrectAnswers", 0),
                userStats.getInteger("totalQuestions", 0),
                rank,
                userStats.get("profileImageUrl", null)

       //         profileImageUrl
        );
    }

    @Override
    public Long countUniqueUsers(Date startDate, Date endDate) {
        var matchDate = Aggregation.match(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );

        var group = Aggregation.group("userId");

        var aggregation = Aggregation.newAggregation(
                matchDate,
                group,
                Aggregation.count().as("uniqueUserCount")
        );

        AggregationResults<Document> result =
                mongoTemplate.aggregate(aggregation, "quizAnswer", Document.class);

        Object uniqueUserCount = result.getMappedResults().isEmpty()
                ? null
                : result.getMappedResults().get(0).get("uniqueUserCount");

        if (uniqueUserCount instanceof Integer) {
            return ((Integer) uniqueUserCount).longValue();
        } else if (uniqueUserCount instanceof Long) {
            return (Long) uniqueUserCount;
        } else {
            return 0L;
        }
    }
}

