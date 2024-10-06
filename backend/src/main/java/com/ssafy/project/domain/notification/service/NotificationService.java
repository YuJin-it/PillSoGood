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

    // 등록된 알림 조회
    @Transactional(readOnly = true)
    public MedicationNotificationResponseDTO getMedicationNotifications(int medicationId) {
        // 유저 복약 정보 가져오기
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다.")); // 해당 복약 정보가 없을 때 예외 처리

        List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);

        return new MedicationNotificationResponseDTO(
                userMedication.getId(),
                userMedication.getName(),
                userMedication.getPrescriptionDay(),
                notifications.stream().map(Notifications::getTime).collect(Collectors.toList())
        );
    }

    // 알림 등록 로직
    @Transactional
    public void registerNotification(NotificationRequestDTO notificationRequestDTO) {
        UserMedication userMedication = userMedicationRepository.findById(notificationRequestDTO.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 복약 정보입니다."));

        // 새로운 구조에서는 fcmToken을 DTO에서 바로 가져옴
        String fcmToken = notificationRequestDTO.getFcmToken();

        for (LocalDateTime notificationTime : notificationRequestDTO.getNotificationsTimeList()) {
            Notifications notification = Notifications.builder()
                    .time(notificationTime)
                    .fcmToken(fcmToken) // 지정한 알림에 대하여 토큰은 동일하게 설정
                    .userMedication(userMedication)
                    .enabled(true)
                    .build();

            notificationRepository.save(notification);
        }
    }

    // 알림 수정
    @Transactional
    public void updateNotification(NotificationRequestDTO notificationRequestDTO) {
        // 요청 DTO에서 알림에 대한 복약 ID와 FCM 토큰 정보를 가져옴
        int medicationId = notificationRequestDTO.getMedicationId();
        String fcmToken = notificationRequestDTO.getFcmToken();

        // 해당 복약 ID로 관련된 알림 정보 조회
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));

        List<Notifications> existingNotifications = notificationRepository.findByUserMedicationAndFcmToken(userMedication, fcmToken);

        // 요청된 알림 시간 리스트를 순회하며 각 알림의 시간을 업데이트
        for (int i = 0; i < existingNotifications.size(); i++) {
            Notifications existingNotification = existingNotifications.get(i);
            LocalDateTime newTime = notificationRequestDTO.getNotificationsTimeList().get(i);

            // 새로 요청된 시간이 기존 시간과 동일한 경우 예외 발생
            if (existingNotification.getTime().equals(newTime)) {
                throw new IllegalArgumentException("새로운 알림 시간이 기존 알림 시간과 동일합니다. 알림 시간을 변경해주세요.");
            }

            // 알림 시간 업데이트
            existingNotification.updateTime(newTime);
            notificationRepository.save(existingNotification);
        }
    }



    // 알림 삭제
    @Transactional
    public void deleteNotification(int medicationId) {
        // 주어진 medicationId로 UserMedication을 조회
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));

        // 해당 복약 ID에 연관된 모든 알림을 삭제
        notificationRepository.deleteByUserMedication(userMedication);
    }

    // 복약 알림 지연
//    @Transactional
//    public void pauseMedication(int medicationId) {
//        UserMedication userMedication = userMedicationRepository.findById(medicationId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다.")); // 해당 복약 정보가 없을 때 예외 처리
//
//        userMedication.updateStatus(Status.STOPPED); //상태 중단으로 변경
//
//        // 알림 비활성화 처리
//        List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);
//        for (Notifications notification : notifications) {
//            notification.updateNotification(false);
//            notificationRepository.save(notification);
//        }
//
//        userMedicationRepository.save(userMedication);
//    }
//
//    // 복약 알림 재개
//    @Transactional
//    public void resumeMedication(int medicationId) {
//        UserMedication userMedication = userMedicationRepository.findById(medicationId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));
//
//        if (userMedication.getStatus() == Status.STOPPED) {
//            userMedication.updateStatus(Status.TAKING);
//
//            List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);
//            for (Notifications notification : notifications) {
//                notification.updateNotification(true);  // 알림 활성화
//                notificationRepository.save(notification);
//            }
//
//            userMedicationRepository.save(userMedication);
//        } else {
//            throw new IllegalArgumentException("중단된 상태가 아닌 복약은 다시 시작할 수 없습니다.");
//        }
//    }

    // 복약 알림 체크
    @Transactional
    public void checkMedication(int medicationId) {
        UserMedication userMedication = userMedicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 정보를 찾을 수 없습니다."));


        // 복약중이고, 활성화된 알림이 존재하는지 확인
        if (userMedication.getStatus() == Status.TAKING) {
            List<Notifications> activeNotifications = notificationRepository.findByUserMedicationAndEnabled(userMedication, true);

            if (!activeNotifications.isEmpty()) {  // 활성화된 알림이 있을 때만 복약 횟수 감소
                if (userMedication.getTotalCount() > 0) {
                    userMedication.decreaseTotalCount();  // 복약 횟수 1 감소
                    userMedicationRepository.save(userMedication);
                }

                // 복약 횟수가 0이 되면 복약 완료 처리
                if (userMedication.getTotalCount() == 0) {
                    completeMedication(userMedication, activeNotifications);
                }
            }
        }
    }

    // 복약 완료 처리 로직
    private void completeMedication(UserMedication userMedication, List<Notifications> activeNotifications) {
        userMedication.updateStatus(Status.COMPLETED);

        // 관련 알림 비활성화
        for (Notifications notification : activeNotifications) {
            notification.updateNotification(false);  // 알림 비활성화
            notificationRepository.save(notification);
        }

        userMedicationRepository.save(userMedication);
    }
}
