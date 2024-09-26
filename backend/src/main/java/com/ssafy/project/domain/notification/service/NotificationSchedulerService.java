package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    // 스케줄러: 1분마다 알림을 확인하여 FCM 전송
    @Transactional
    @Scheduled(fixedRate = 60000)  // 1분마다 실행 (밀리초 단위, 1분 간격)
    public void checkAndSendNotifications() {
        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);  // 초 단위까지 비교

        // 현재 시간과 일치하는 알림 가져오기
        List<Notifications> notifications = notificationRepository.findByTime(now);

        for (Notifications notification : notifications) {
            // 알림 메시지 생성 및 전송
            String title = "복약 알림";
            String body = notification.getNotificationMessage();
            String token = notification.getUserMedication().getUserDetail().getFcmToken();  // 사용자 기기 토큰 가져오기

            // FCM을 통해 알림 전송
            fcmService.sendNotification(title, body, token);

            // 알림이 전송된 상태로 업데이트 (optional) -> 다음 날
            notification.updateTime(notification.getTime().plusDays(1));
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void updateNotificationTime(Notifications notification) {
        LocalDateTime nextNotificationTime = notification.getTime().plusDays(1);

        // 다음 알림 시간 갱신
        notification.updateTime(nextNotificationTime);
        notificationRepository.save(notification);
    }

}
