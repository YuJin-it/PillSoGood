package com.ssafy.project.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import com.ssafy.project.domain.notification.dto.NotificationResponseDTO;
import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final UserMedicationRepository userMedicationRepository;  // UserMedicationRepository 주입

//    public NotificationResponseDTO sendNotification(NotificationRequestDTO notificationRequestDTO) {
//        String deviceToken = notificationRequestDTO.getDeviceToken();
//
//        // 약물 정보 조회
//        UserMedication userMedication = userMedicationRepository.findById(notificationRequestDTO.getMedicationId())
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 약물 정보입니다."));
//
//        // Notification 엔티티 생성
//        Notifications notification = Notifications.builder()
//                .time(LocalDateTime.parse(notificationRequestDTO.getAlertTime()))  // 알림 시간 설정
//                .userMedication(userMedication)
//                .build();
//
//        // 알림 메시지 생성
//        String notificationMessage = notification.getNotificationMessage();
//
//        // FCM 메시지 전송을 분리된 메소드로 관리
//        try {
//            sendFcmMessage(deviceToken, notificationMessage);
//            return new NotificationResponseDTO("메시지 전송 성공!", true);
//        } catch (Exception e) {
//            // 예외 처리 강화 (로그 추가 등)
//            return new NotificationResponseDTO("메시지 전송 실패: " + e.getMessage(), false);
//        }
//    }

    private void sendFcmMessage(String deviceToken, String messageBody) throws Exception {
        Notification fcmNotification = Notification.builder()
                .setTitle("복용 알림")
                .setBody(messageBody)
                .build();

        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(fcmNotification)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
