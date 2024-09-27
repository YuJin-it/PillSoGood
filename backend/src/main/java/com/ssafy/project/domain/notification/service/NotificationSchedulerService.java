package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.notification.repository.NotificationRepository;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final UserMedicationRepository userMedicationRepository;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    // 스케줄러: 1분마다 알림을 확인하여 FCM 전송
    @Transactional
    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void checkAndSendNotifications() {
        // 현재 시간 가져오기 (초와 나노초를 0으로 설정하여 정확히 비교)
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        // 현재 시간과 일치하는 알림 가져오기
        List<Notifications> notifications = notificationRepository.findByTime(now);

        for (Notifications notification : notifications) {
            UserMedication userMedication = notification.getUserMedication();

            // 복약 상태가 'TAKING'일 때만 알림 전송 및 복약 횟수 감소
            if (userMedication.getStatus() == Status.TAKING) {
                // 알림 전송
                String title = "복약 알림";
                String body = userMedication.getName() + " 약물을 복용할 시간입니다.";
                String token = userMedication.getUserDetail().getFcmToken();  // FCM 토큰 가져오기

                // FCM을 통해 알림 전송
                fcmService.sendNotification(title, body, token);

                // 복약 횟수 감소 처리 (남은 복약 횟수가 있을 때만 감소)
                if (userMedication.getTotalCount() > 0) {
                    userMedication.decreaseTotalCount();  // 복약 횟수 1 감소
                    userMedicationRepository.save(userMedication);  // 변경된 복약 횟수 저장
                }

                // 알림 시간을 다음 알림 시간으로 재설정 (예: 1일 뒤)
                notification.updateTime(notification.getTime().plusDays(1));
                notificationRepository.save(notification);
            }
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
