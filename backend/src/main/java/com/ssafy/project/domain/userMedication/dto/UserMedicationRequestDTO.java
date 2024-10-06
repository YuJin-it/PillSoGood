package com.ssafy.project.domain.userMedication.dto;

import com.ssafy.project.domain.userDetail.entity.UserDetail;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import com.ssafy.project.domain.userMedicationDetail.entity.UserMedicationDetail;
import com.ssafy.project.domain.userMedicationDetail.dto.UserMedicationDetailRequestDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMedicationRequestDTO {

    private String name;
    private Status status;
    private int userDetailId;
    private LocalDateTime intakeAt;
    private int prescriptionDay;
    private String hospitalName;
    private String pharmacyName;
    private List<UserMedicationDetailRequestDTO> userMedicationDetailList = new ArrayList<>();

    public UserMedication toEntity(List<UserMedicationDetail> userMedicationDetail, UserDetail userDetail) {
        return UserMedication.builder()
                .name(this.name)
                .status(this.status)
                .userDetail(userDetail)
                .intakeAt(this.intakeAt)
                .prescriptionDay(this.prescriptionDay)
                .hospitalName(this.hospitalName)
                .pharmacyName(this.pharmacyName)
                .userMedicationDetailList(userMedicationDetail)
                .build();
    }
}
