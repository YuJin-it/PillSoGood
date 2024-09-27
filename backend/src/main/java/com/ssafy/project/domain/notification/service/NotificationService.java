package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.dto.MedicationNotificationResponseDTO;
import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.notification.repository.NotificationRepository;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserMedicationRepository userMedicationRepository;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    // 등록된 알림 조회
    @Transactional(readOnly = true)
    public MedicationNotificationResponseDTO getMedicationNotifications(int medicationId) {
        // 유저 복약 정보 가져오기
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다.")); // 해당 복약 정보가 없을 때 예외 처리

        List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);

        return new MedicationNotificationResponseDTO(
                userMedication.getName(),
                userMedication.getIntakeAt(),
                userMedication.getPrescriptionDay(),
                userMedication.getStatus(),
                notifications.stream().map(Notifications::getTime).collect(Collectors.toList())
        );
    }

    // 알림 등록 로직
    @Transactional
    public void registerNotification(NotificationRequestDTO notificationRequestDTO) {
        UserMedication userMedication = userMedicationRepository.findById(notificationRequestDTO.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 복약 정보입니다."));

        for (NotificationRequestDTO.NotificationsDTO notificationDTO : notificationRequestDTO.getNotificationsDTOList()) {
            Notifications notification = Notifications.builder()
                    .time(notificationDTO.getTime())
                    .userMedication(userMedication)
                    .build();

            notificationRepository.save(notification);
        }
    }

    // 알림 수정
    @Transactional
    public void updateNotification(NotificationRequestDTO notificationRequestDTO) {
        for (NotificationRequestDTO.NotificationsDTO notificationDTO : notificationRequestDTO.getNotificationsDTOList()) {
            Notifications notification = notificationRepository.findById(notificationDTO.getNotificationId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 알림 정보를 찾을 수 없습니다."));

            notification.updateTime(notificationDTO.getTime());

            notificationRepository.save(notification);
        }
    }


    // 알림 삭제
    @Transactional
    public void deleteNotification(int notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림 정보를 찾을 수 없습니다."));

        notificationRepository.delete(notification);
    }

    // 복약 지연
    @Transactional
    public void delayMedication(int medicationId) {
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다.")); // 해당 복약 정보가 없을 때 예외 처리

        userMedication.updateStatus(Status.STOPPED); //상태 중단으로 변경

        // 알림 비활성화 처리
        List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);
        for (Notifications notification : notifications) {
            notification.updateTime(null);
            notificationRepository.save(notification);
        }

        userMedicationRepository.save(userMedication);
    }

    // 복약 재개
    @Transactional
    public void restartMedication(int medicationId) {
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));

        if (userMedication.getStatus() == Status.STOPPED) {
            userMedication.updateStatus(Status.TAKING);  // 상태를 복약 중으로 변경

            List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);
            for (Notifications notification : notifications) {
                LocalDateTime originalTime = notification.getTime() == null ? LocalDateTime.now() : notification.getTime();
                LocalDateTime newTime = LocalDateTime.now()
                        .withHour(originalTime.getHour())    // 기존 알림 시간 유지
                        .withMinute(originalTime.getMinute())
                        .withSecond(0).withNano(0);          // 초와 나노초는 0으로 설정

                notification.updateTime(newTime);  // 알림 시간 재설정
                notificationRepository.save(notification);
            }

            userMedicationRepository.save(userMedication);
        } else {
            throw new IllegalArgumentException("중단된 상태가 아닌 복약은 다시 시작할 수 없습니다.");
        }
    }

    // 복약 완료
    @Transactional
    public void completeMedication(int medicationId) {
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));

        // 알림이 옴 -> count 감소 -> count가 0이면 복약 종료(상태 변경)
        if (userMedication.getTotalCount() == 0) {
            userMedication.updateStatus(Status.COMPLETED);
            userMedicationRepository.save(userMedication);

            // 해당 알약 번호에 대한 알림 모두 삭제
            List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);
            notificationRepository.deleteAll(notifications);
        }
    }

    // 복약 알림
    @Transactional
    public void medicationNotification(int medicationId) {
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));

        String title = "복약 알림";
        String body = userMedication.getName() + " 약물을 복용할 시간입니다.";
        String token = userMedication.getUserDetail().getFcmToken();  // FCM 토큰 가져오기

        // FCM 알림 전송
        fcmService.sendNotification(title, body, token);
    }
}
