package com.ssafy.project.domain.notification.dto;

import com.ssafy.project.domain.userMedication.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicationNotificationResponseDTO {
    private String name;
    private String intakeAt;
    private int prescriptionDay;
    private Status status;
    private List<LocalDateTime> alertTimes;
}
