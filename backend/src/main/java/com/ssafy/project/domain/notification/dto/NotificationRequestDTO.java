package com.ssafy.project.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    private int medicationId;  // 복약 ID
    private String fcmToken;   // FCM 토큰
    private List<LocalDateTime> notificationsTimeList;  // 알림 시간 목록
}
