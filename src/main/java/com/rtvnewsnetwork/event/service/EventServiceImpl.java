package com.rtvnewsnetwork.event.service;

import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.event.repository.EventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final KafkaTemplate<String, EventModel> kafkaTemplate;
    private final EventRepository eventRepository;

    public EventServiceImpl(KafkaTemplate<String, EventModel> kafkaTemplate, EventRepository eventRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventRepository = eventRepository;
    }

    @Override
    public EventModel publishEvent(String topic, EventModel data) {
        try {
            EventModel eventModel = save(data);
            kafkaTemplate.send(topic, eventModel);
        } catch (Exception ex) {
            System.out.println("Error sending to " + topic + ": " + ex.getMessage());
        }
        return data;
    }

    @Override
    public EventModel save(EventModel eventModel) {
        return eventRepository.save(eventModel);
    }
}
