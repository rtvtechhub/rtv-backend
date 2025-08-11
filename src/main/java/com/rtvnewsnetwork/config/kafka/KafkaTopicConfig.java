package com.rtvnewsnetwork.config.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${event.kafka.topic}")
    private String eventTopic;

    public static String GENERIC_EVENT_CHANNEL;

    @PostConstruct
    public void init() {
        GENERIC_EVENT_CHANNEL = eventTopic;
    }

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(GENERIC_EVENT_CHANNEL)
                .build();
    }
}

