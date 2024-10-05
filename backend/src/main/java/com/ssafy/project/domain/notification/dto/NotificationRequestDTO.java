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

    private int medicationId;
    private List<NotificationsDTO> notificationsDTOList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationsDTO {
        private int notificationId;
        private LocalDateTime time;
        private String fcmToken;
    }
}
