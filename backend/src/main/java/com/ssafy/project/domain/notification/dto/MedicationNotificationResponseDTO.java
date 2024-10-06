package com.ssafy.project.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicationNotificationResponseDTO {
    private int id;
    private String name; //약봉투
    private int prescriptionDay; //처방일수
    private List<LocalDateTime> alertTimes; //알림 시간 리스트
}
