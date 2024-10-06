package com.ssafy.project.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.project.domain.notification.entity.Notifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    @Transactional
    public void sendNotification(String title, String body, String token) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("알림 전송 완료: {}", response);
        } catch (Exception e) {
            log.error("알림 전송 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void sendNotification(Notifications notification, String title, String body) {
        sendNotification(title, body, notification.getFcmToken());
    }
}

