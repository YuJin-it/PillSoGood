package com.ssafy.project.domain.medicine.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineListDTO {
    private List<Integer> medicineIds;
}
