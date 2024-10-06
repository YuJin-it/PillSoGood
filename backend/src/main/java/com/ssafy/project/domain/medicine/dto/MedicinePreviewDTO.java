package com.ssafy.project.domain.medicine.dto;

import com.ssafy.project.domain.lists.ageProhibition.dto.AgeProhibitionDTO;
import com.ssafy.project.domain.lists.amountProhibition.dto.AmountProhibitionDTO;
import com.ssafy.project.domain.lists.pregnancyProhibition.dto.PregnancyProhibitionDTO;
import com.ssafy.project.domain.lists.seniorProhibition.dto.SeniorProhibitionDTO;
import com.ssafy.project.domain.medicine.entity.Medicine;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicinePreviewDTO {

    private int id;
    private String korName;
    private String engName;
    private String imageUrl;
    private AgeProhibitionDTO ageProhibition;
    private AmountProhibitionDTO amountProhibition;
    private PregnancyProhibitionDTO pregnancyProhibition;
    private SeniorProhibitionDTO seniorProhibition;

    public static MedicinePreviewDTO toMedicinePreviewDTO(Medicine medicine) {
        return MedicinePreviewDTO.builder()
                .id(medicine.getId())
                .korName(medicine.getKorName())
                .engName(medicine.getEngName())
                .imageUrl(medicine.getImageUrl())
                .ageProhibition(medicine.getAgeProhibition() != null ?
                        AgeProhibitionDTO.toDto(medicine.getAgeProhibition()) : null)
                .amountProhibition(medicine.getAmountProhibition() != null ?
                        AmountProhibitionDTO.toDto(medicine.getAmountProhibition()) : null)
                .pregnancyProhibition(medicine.getPregnancyProhibition() != null ?
                        PregnancyProhibitionDTO.toDto(medicine.getPregnancyProhibition()) : null)
                .seniorProhibition(medicine.getSeniorProhibition() != null ?
                        SeniorProhibitionDTO.toDto(medicine.getSeniorProhibition()) : null)
                .build();
    }
}
