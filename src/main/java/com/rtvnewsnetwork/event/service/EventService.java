package com.rtvnewsnetwork.event.service;

import com.rtvnewsnetwork.event.model.EventModel;

public interface EventService {
    EventModel publishEvent(String topic, EventModel data);
    EventModel save(EventModel eventModel);
}
