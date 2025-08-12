package com.rtvnewsnetwork.notification.service;

import com.google.firebase.messaging.*;

import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;
import com.rtvnewsnetwork.config.kafka.KafkaTopicConfig;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import com.rtvnewsnetwork.notification.repository.FcmRepository;
import com.rtvnewsnetwork.notification.repository.NotificationRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService , EventPublisher {

    private final UserRepository userRepository;
    private final FcmRepository fcmRepository;
    private final NotificationRepository notificationRepository;
    private final EventService eventService;

    public NotificationServiceImpl(UserRepository userRepository,
                                   FcmRepository fcmRepository,
                                   EventService eventService,
                                   NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.fcmRepository = fcmRepository;
        this.eventService = eventService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendNotification(String userId, NotificationModel payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> tokenList = new ArrayList<>(user.getFcmToken().values());

        Notification notification = Notification.builder()
                .setTitle(payload.getTitle())
                .setBody(payload.getDescription())
                .setImage(payload.getImageUrl())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("logo", String.valueOf(payload.getLogoUrl()))
                .putData("path", payload.getPath())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Successfully sent message to " + response.getSuccessCount() + " devices");
            if (response.getFailureCount() > 0) {
                System.out.println("Failed to send message to " + response.getFailureCount() + " devices");
                for (int i = 0; i < response.getResponses().size(); i++) {
                    SendResponse sendResponse = response.getResponses().get(i);
                    if (!sendResponse.isSuccessful()) {
                        System.out.println("Failed token: " + tokenList.get(i) + ", Error: " + sendResponse.getException());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotification(NotificationModel payload) {
        notificationRepository.save(payload);

        List<FcmModel> fcmModels = fcmRepository.findAll();
        List<String> tokenList = fcmModels.stream()
                .map(FcmModel::getToken)
                .collect(Collectors.toList());

        Notification notification = Notification.builder()
                .setTitle(removeHtml(payload.getTitle()))
                .setBody(payload.getDescription() != null ? removeHtml(payload.getDescription()) : null)
                .setImage(payload.getImageUrl())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("path", payload.getPath())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            if (response.getFailureCount() > 0) {
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < response.getResponses().size(); i++) {
                    SendResponse sendResponse = response.getResponses().get(i);
                    if (!sendResponse.isSuccessful()) {
                        MessagingErrorCode errorCode = sendResponse.getException() != null
                                ? sendResponse.getException().getMessagingErrorCode() : null;

                        if (errorCode == MessagingErrorCode.INVALID_ARGUMENT ||
                                errorCode == MessagingErrorCode.UNREGISTERED ||
                                errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                            failedTokens.add(tokenList.get(i));
                        }
                    }
                }

                if (!failedTokens.isEmpty()) {
                    System.out.println("List of tokens that caused failures: " + failedTokens);
                    failedTokens.forEach(fcmRepository::deleteByToken);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotification(List<String> userList, NotificationModel payload) {
        List<String> tokenList = getAllTokensByUserIds(userList);

        Notification notification = Notification.builder()
                .setTitle(removeHtml(payload.getTitle()))
                .setBody(payload.getDescription() != null ? removeHtml(payload.getDescription()) : null)
                .setImage(payload.getImageUrl())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("path", payload.getPath())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Successfully sent message to " + response.getSuccessCount() + " devices");
            if (response.getFailureCount() > 0) {
                System.out.println("Failed to send message to " + response.getFailureCount() + " devices");
                for (int i = 0; i < response.getResponses().size(); i++) {
                    SendResponse sendResponse = response.getResponses().get(i);
                    if (!sendResponse.isSuccessful()) {
                        System.out.println("Failed token: " + tokenList.get(i) + ", Error: " + sendResponse.getException());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFcmToken(FcmModel fcmModel) {
        FcmModel existingFcm = fcmRepository.findByDeviceId(fcmModel.getDeviceId());
        if (existingFcm != null) {
            existingFcm.setToken(fcmModel.getToken());
            fcmRepository.save(existingFcm);
        } else {
            fcmRepository.save(fcmModel);
        }
    }

    @Override
    public void sendCustomNotification(NotificationModel notificationDto) {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("title", notificationDto.getTitle());
        eventData.put("description", notificationDto.getDescription());

        if (notificationDto.getImageUrl() != null) {
            eventData.put("imageUrl", notificationDto.getImageUrl());
        }
        if (notificationDto.getPath() != null) {
            eventData.put("path", notificationDto.getPath());
        }

        produceEventToKafka(EventType.NEW_POST, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);
    }

    @Override
    public List<NotificationModel> getAllNotification(Pageable pageable) {
        return notificationRepository.findAll(pageable).toList();
    }

    private String removeHtml(String input) {
        return input.replaceAll("<[^>]*>", "").trim();
    }

    private List<String> getAllTokensByUserIds(List<String> userIds) {
        List<User> users = userRepository.findAllByIdIn(userIds);
        return users.stream()
                .flatMap(user -> user.getFcmToken().values().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotifications(List<String> notificationIds) {
        notificationRepository.deleteAllById(notificationIds);
    }

    @Override
    public EventService getEventService() {
        return this.eventService;
    }
}
