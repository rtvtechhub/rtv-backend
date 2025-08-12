package com.rtvnewsnetwork.notification.service;

import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.notification.model.NotificationEventType;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import com.rtvnewsnetwork.notification.repository.NotificationRepository;
import org.joda.time.Instant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public NotificationConsumer(
            NotificationService notificationService,
            NotificationRepository notificationRepository
       ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(
            topics = "${event.kafka.topic.notifications}",
            groupId = "${rtv.dev.notification.kafka.groupid}"
    )
    public void processNotificationEvent(EventModel eventModel) {
        System.out.println("Inside notification consumer");

        NotificationModel payload = createPayload(eventModel);
        NotificationEventType notificationType =
                NotificationEventType.getNotificationType(eventModel.getEventType());

        switch (notificationType) {
            case ALL_USER:
                notificationService.sendNotification(payload);
                break;
            case SINGLE_USER:
                notificationService.sendNotification(eventModel.getUserId(), payload);
                break;
            case GROUP_USER:
                notificationService.sendNotification(extractUserList(eventModel), payload);
                break;
            default:
                System.out.println("Does not match any notification type");
        }
    }

    private NotificationModel createPayload(EventModel eventModel) {
        Map<String, Object> data = (Map<String, Object>) eventModel.getData();
        if (data == null) data = Collections.emptyMap();

        String title = (String) data.getOrDefault("title", "");
        String description = (String) data.getOrDefault("description", "");
        String imageUrl = (String) data.get("imageUrl");
        String logoUrl = "https://interkashi-media.s3.ap-south-1.amazonaws.com/images/gKLpRufRtCOnOOTogPssDQAEDbyNoCHT.jpeg";
        String path = (String) data.get("path");

        return new NotificationModel(
                title,
                description,
                imageUrl,
                logoUrl,
                path,
                eventModel.getCreatedAt() != null ? eventModel.getCreatedAt() : Instant.now()
        );
    }

    private List<String> extractUserList(EventModel eventModel) {
        Map<String, Object> data = (Map<String, Object>) eventModel.getData();
        if (data == null) return Collections.emptyList();
        return (List<String>) data.getOrDefault("userlist", Collections.emptyList());
    }
}
