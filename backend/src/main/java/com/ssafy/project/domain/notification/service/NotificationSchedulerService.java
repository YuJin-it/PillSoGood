package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.notification.repository.NotificationRepository;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    // 스케줄러: 1분마다 알림을 확인하여 FCM 전송
    @Transactional
    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void checkAndSendNotifications() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        log.info("스케줄러 실행 - 현재 시간 : {}", now);

        // 현재 시간과 일치하며, 활성화(enabled) 상태이고, 복약중인 알림만 가져오기
        List<Notifications> notifications = notificationRepository.findByTimeAndUserMedicationStatusAndEnabled(now, Status.TAKING, true);

        for (Notifications notification : notifications) {
            UserMedication userMedication = notification.getUserMedication();

            if (userMedication.getStatus() == Status.STOPPED || userMedication.getStatus() == Status.COMPLETED) {
                continue;
            }

            String title = "복약 알림";
            String body = userMedication.getName() + " 약물을 복용할 시간입니다.";
            String token = userMedication.getUserDetail().getFcmToken();

            // FCM 알림 전송
            fcmService.sendNotification(title, body, token);

            log.info("알림 전송 완료 : {}", notification.getTime());

            // 알림 시간을 다음 알림 시간으로 재설정
            notification.updateTime(notification.getTime().plusDays(1));
            notificationRepository.save(notification);
        }
    }
}
