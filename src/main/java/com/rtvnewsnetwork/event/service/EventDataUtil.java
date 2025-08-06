package com.rtvnewsnetwork.event.service;

import java.util.HashMap;
import java.util.Map;

public class EventDataUtil {

    private EventDataUtil() {
        // Utility class, prevent instantiation
    }

    public static Map<String, Object> createCommentEventData(Object commentId, Object postId) {
        return createDataMap(
                "commentId", commentId,
                "postId", postId
        );
    }

    public static Map<String, Object> createPostEventData(Object postId) {
        return createDataMap(
                "postId", postId
        );
    }

    public static Map<String, Object> createUserProfileEventData(String userId, String name, String email) {
        return createDataMap(
                "userId", userId,
                "name", name,
                "email", email
        );
    }

    public static Map<String, Object> createQuizCompletionEventData(String userId, int coinsEarned) {
        return createDataMap(
                "userId", userId,
                "coinsEarned", coinsEarned
        );
    }

    // Helper method to create Map from key-value pairs
    private static Map<String, Object> createDataMap(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            Object value = keyValues[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
