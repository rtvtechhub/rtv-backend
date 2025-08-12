package com.rtvnewsnetwork.notification.service;

import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationService {

    void sendNotification(String userId, NotificationModel payload);

    void addFcmToken(FcmModel fcmModel);

    void sendNotification(NotificationModel payload);

    void sendNotification(List<String> userList, NotificationModel payload);

    void sendCustomNotification(NotificationModel notificationDto);

    List<NotificationModel> getAllNotification(Pageable pageable);

    void deleteNotifications(List<String> notificationIds);
}
