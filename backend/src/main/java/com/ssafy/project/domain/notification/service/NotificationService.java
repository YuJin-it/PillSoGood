package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.dto.MedicationNotificationResponseDTO;
import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import com.ssafy.project.domain.notification.dto.NotificationResponseDTO;
import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.notification.repository.NotificationRepository;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedication.repository.UserMedicationRepository;
import com.ssafy.project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        // 복약에 대한 알림 리스트 가져오기
        List<Notifications> notifications = notificationRepository.findByUserMedication(userMedication);

        // DTO로 변환하여 반환
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
        // 유저 복약 정보 가져오기
        UserMedication userMedication = userMedicationRepository.findById(notificationRequestDTO.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 복약 정보입니다."));

        // 알림 리스트 등록
        for (NotificationRequestDTO.NotificationsDTO notificationDTO : notificationRequestDTO.getNotificationsDTOList()) {
            Notifications notification = Notifications.builder()
                    .time(notificationDTO.getTime())
                    .userMedication(userMedication)
                    .build();

            notificationRepository.save(notification);
        }
    }
}
