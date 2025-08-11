package com.rtvnewsnetwork.event.service;

import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.event.model.EventType;

import java.time.Instant;

public interface EventPublisher {

    EventService getEventService();

    // Default method in Java interfaces
    default void produceEventToKafka(EventType type, String userId, String topic, Object data) {
        EventModel event = new EventModel();
        event.setEventType(type);
        event.setUserId(userId);
        event.setCreatedAt(Instant.now());
        event.setData(data);

        getEventService().publishEvent(topic, event);
    }

    // Overload without data (to mimic Kotlin default parameter)
    default void produceEventToKafka(EventType type, String userId, String topic) {
        produceEventToKafka(type, userId, topic, null);
    }
}
