package com.ssafy.project.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMService {

    // FCM을 통해 알림 전송
    public void sendNotification(String title, String body, String token) {
        try {
            // 알림 메시지 구성
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();

            // 알림 전송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("알림 전송 완료: " + response);
        } catch (Exception e) {
            System.err.println("알림 전송 실패: " + e.getMessage());
        }
    }
}
