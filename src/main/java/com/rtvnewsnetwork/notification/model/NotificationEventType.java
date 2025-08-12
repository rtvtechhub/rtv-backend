package com.rtvnewsnetwork.notification.model;

import com.rtvnewsnetwork.event.model.EventType;

import java.util.*;

public enum NotificationEventType {
    ALL_USER,
    SINGLE_USER,
    GROUP_USER;

    // Map from a list of EventTypes to a NotificationEventType
    private static final Map<List<EventType>, NotificationEventType> eventTypeToNotificationTypeMap;

    static {
        Map<List<EventType>, NotificationEventType> map = new HashMap<>();
        map.put(Collections.singletonList(EventType.NEW_POST), ALL_USER);
        map.put(Arrays.asList(
                EventType.USER_PROFILE_COMPLETE_NOTIFICATION,
                EventType.REVIEW,
                EventType.COIN_DEBIT,
                EventType.COIN_CREDIT
        ), SINGLE_USER);
        map.put(Collections.singletonList(EventType.OTHERS), GROUP_USER);

        eventTypeToNotificationTypeMap = Collections.unmodifiableMap(map);
    }

    // Method to get NotificationEventType from EventType
    public static NotificationEventType getNotificationType(EventType eventType) {
        for (Map.Entry<List<EventType>, NotificationEventType> entry : eventTypeToNotificationTypeMap.entrySet()) {
            if (entry.getKey().contains(eventType)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
