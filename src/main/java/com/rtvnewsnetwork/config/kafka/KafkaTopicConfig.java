package com.rtvnewsnetwork.config.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Value("${event.kafka.topic}")
//    private String eventTopic;
//
//    public static String GENERIC_EVENT_CHANNEL;
//
//    @PostConstruct
//    public void init() {
//        GENERIC_EVENT_CHANNEL = eventTopic;
//    }
//
//    @Bean
//    public NewTopic createTopic() {
//        return TopicBuilder.name(GENERIC_EVENT_CHANNEL)
//                .build();
//    }
//}
//


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaTopicConfig {

    @Value("${event.kafka.topic.notifications}")
    private String notificationsTopic;

    @Value("${event.kafka.topic.coinupdates}")
    private String coinUpdatesTopic;

    public static String NOTIFICATIONS_CHANNEL;
    public static String COIN_UPDATES_CHANNEL;

    @PostConstruct
    public void init() {
        NOTIFICATIONS_CHANNEL = notificationsTopic;
        COIN_UPDATES_CHANNEL = coinUpdatesTopic;
    }

    @Bean
    public NewTopic createNotificationsTopic() {
        return TopicBuilder.name(NOTIFICATIONS_CHANNEL)
                .partitions(3) // set number of partitions
                .replicas(1)   // replication factor (for single broker: 1)
                .build();
    }

    @Bean
    public NewTopic createCoinUpdatesTopic() {
        return TopicBuilder.name(COIN_UPDATES_CHANNEL)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
