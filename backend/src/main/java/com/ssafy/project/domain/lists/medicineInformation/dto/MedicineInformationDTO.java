package com.ssafy.project.domain.lists.medicineInformation.dto;

import com.ssafy.project.domain.lists.medicineInformation.entity.MedicineInformation;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInformationDTO {

    private int informationId;
    private String information;

    public static MedicineInformationDTO toMedicineInformationDTO(MedicineInformation medicineInformation) {
        return MedicineInformationDTO.builder()
                .informationId(medicineInformation.getId())
                .information(medicineInformation.getInformation())
                .build();
    }
}
