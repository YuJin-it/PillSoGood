package com.ssafy.project.domain.medicine.dto;

import com.ssafy.project.domain.lists.ageProhibition.dto.AgeProhibitionDTO;
import com.ssafy.project.domain.lists.amountProhibition.dto.AmountProhibitionDTO;
import com.ssafy.project.domain.lists.medicineInformation.dto.MedicineInformationDTO;
import com.ssafy.project.domain.lists.pregnancyProhibition.dto.PregnancyProhibitionDTO;
import com.ssafy.project.domain.lists.seniorProhibition.dto.SeniorProhibitionDTO;
import com.ssafy.project.domain.medicine.entity.Medicine;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDTO {

    private int id;
    private String code;
    private String korName;
    private String engName;
    private String category;
    private String company;
    private String drugForm;
    private String characters;
    private String kind;
    private String effect;
    private String usages;
    private String imageUrl;
    private List<MedicineInformationDTO> medicineInformation;
    private AgeProhibitionDTO ageProhibition;
    private AmountProhibitionDTO amountProhibition;
    private PregnancyProhibitionDTO pregnancyProhibition;
    private SeniorProhibitionDTO seniorProhibition;

    public static MedicineDTO toMedicineDTO(Medicine medicine) {
        return MedicineDTO.builder()
                .id(medicine.getId())
                .code(medicine.getCode())
                .korName(medicine.getKorName())
                .engName(medicine.getEngName())
                .category(medicine.getCategory().toString())
                .company(medicine.getCompany())
                .drugForm(medicine.getDrugForm())
                .characters(medicine.getCharacters())
                .kind(medicine.getKind())
                .effect(medicine.getEffect())
                .usages(medicine.getUsages())
                .imageUrl(medicine.getImageUrl())
                .medicineInformation(medicine.getMedicineInformation().stream()
                        .map(MedicineInformationDTO::toMedicineInformationDTO) // MedicineInformation을 DTO로 변환
                        .collect(Collectors.toList()))
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
