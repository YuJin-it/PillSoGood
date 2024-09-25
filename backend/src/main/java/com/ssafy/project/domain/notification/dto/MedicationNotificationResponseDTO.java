package com.ssafy.project.domain.notification.dto;

import com.ssafy.project.domain.userMedication.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicationNotificationResponseDTO {
    private String name; //약봉투
    private LocalDateTime intakeAt; //복용날짜
    private int prescriptionDay; //처방일수
    private Status status; //복용상태(복용중, 중지, 완료)
    private List<LocalDateTime> alertTimes; //알림 시간 리스트
}
