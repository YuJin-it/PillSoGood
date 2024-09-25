package com.ssafy.project.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    private int medicationId;
    private String deviceToken;    // 클라이언트에서 전달받는 디바이스 토큰
    private String medicationName; // 약물 이름
    private String alertTime;      // 알림 시간 (예: "08:00")
}
