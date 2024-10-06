package com.ssafy.project.domain.lists.combinationProhibition.dto;

import com.ssafy.project.domain.lists.ageProhibition.dto.AgeProhibitionDTO;
import com.ssafy.project.domain.lists.ageProhibition.entity.AgeField;
import com.ssafy.project.domain.lists.ageProhibition.entity.AgeProhibition;
import com.ssafy.project.domain.lists.ageProhibition.entity.AgeRange;
import com.ssafy.project.domain.lists.combinationProhibition.entity.CombinationProhibition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationProhibitionDTO {
    private int id;
    private String effect;
    private int medicineIdA;
    private int medicineIdB;
    private String nameA;
    private String nameB;

    public static CombinationProhibitionDTO toDto(CombinationProhibition combinationProhibition) {
        return CombinationProhibitionDTO.builder()
                .id(combinationProhibition.getId())
                .effect(combinationProhibition.getEffect())
                .medicineIdA(combinationProhibition.getMedicineA().getId())
                .medicineIdB(combinationProhibition.getMedicineB().getId())
                .nameA(combinationProhibition.getNameA())
                .nameB(combinationProhibition.getNameB())
                .build();
    }
}
