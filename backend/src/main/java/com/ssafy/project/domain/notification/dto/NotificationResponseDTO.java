package com.ssafy.project.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private String message;   // 전송 결과 메시지
    private boolean success;  // 전송 성공 여부
}
